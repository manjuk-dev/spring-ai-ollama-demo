package com.example.demo.features.agent;

import org.springframework.stereotype.Service;
import org.springframework.ai.tool.annotation.Tool;
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
/**
 * SystemInfoTool - Provides real-time system health metrics for AI assistant
 *
 * Issue: 500ms Thread.sleep() in getSystemStatus() creates visible latency in AI response.
 * Proposed Solution: Move measurement logic to a @Scheduled background task that runs every second.
 * Cache the results in a thread-safe variable so the @Tool can return data instantly without blocking.
 */
@Service
public class SystemInfoTool {

    /**
     * Retrieves current system CPU usage and available memory.
     * This method is exposed as a tool that AI can call to check system health.
     *
     * Current Implementation Issue:
     * - Thread.sleep(500) blocks the calling thread for 500ms on every invocation
     * - This delay is necessary because getCpuLoad() needs time to calculate accurate CPU usage
     * - Creates noticeable latency in AI responses
     *
     * @return String containing formatted system status with CPU percentage and RAM in MB
     */
    @Tool(description = "Get the current system CPU usage and total available system memory")
    public String getSystemStatus() {

        // Cast ManagementFactory bean to Sun-specific OperatingSystemMXBean
        // This gives us access to getCpuLoad() and memory methods not available in standard MXBean
        OperatingSystemMXBean osBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

        // First call to getCpuLoad() - initializes the CPU measurement baseline
        // The first call often returns 0 or inaccurate values, so we discard this result
        osBean.getCpuLoad();

        try {
            // Sleep for 500ms to allow CPU load calculation to stabilize
            // TODO: Replace with background @Scheduled task to eliminate this blocking delay
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Restore interrupted status if thread is interrupted during sleep
            Thread.currentThread().interrupt();
        }

        // Second call to getCpuLoad() - returns actual CPU usage as a decimal (0.0 to 1.0)
        // Where 0.0 = 0% CPU usage and 1.0 = 100% CPU usage
        double cpu = osBean.getCpuLoad();

        // Fallback: Some fast systems still return 0 after sleep
        // Make one more attempt to get a valid reading
        if (cpu <= 0) {
            cpu = osBean.getCpuLoad();
        }

        // Get free physical memory (RAM) for the entire system (not just JVM heap)
        // Convert from bytes to megabytes by dividing by (1024 * 1024)
        long freeRAM = osBean.getFreeMemorySize() / (1024 * 1024);

        // Get total physical memory installed on the system
        // Also convert from bytes to megabytes
        long totalRAM = osBean.getTotalMemorySize() / (1024 * 1024);

        // Debug output to console (TODO: Replace with proper logging framework)
        System.out.println("cpu" + cpu * 100);
        System.out.println("freeRam" + freeRAM);
        System.out.println("totalRam" + totalRAM);

        // Return formatted string with system metrics
        // Math.max(0, cpu * 100) ensures we never return negative CPU percentages
        // Multiply cpu by 100 to convert from decimal (0.0-1.0) to percentage (0-100)
        return "Current System Health: CPU Usage is :" + Math.max(0, cpu * 100)
                + " Free RAM is :" + freeRAM + "MB out of" + totalRAM + "MB";
    }
}

