
package org.dingdang.common.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: zhangcheng
 * @Date: 2024/4/23 0:43 星期二
 * @Description:
 */
@Component
public class IdWorker {
  private final long twepoch = 1288834974657L; // 时间戳初始值
    private final long workerIdBits = 5L; // 工作节点ID位数
    private final long datacenterIdBits = 5L; // 数据中心ID位数
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits); // 工作节点最大值
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits); // 数据中心最大值
    private final long sequenceBits = 12L; // 序列号位数
    private final long workerIdShift = sequenceBits;
    private final long datacenterIdShift = sequenceBits + workerIdBits;
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;
    private final long sequenceMask = -1L ^ (-1L << sequenceBits); // 序列号掩码

    private long workerId; // 工作节点ID
    private long datacenterId; // 数据中心ID
    private long sequence = 0L; // 当前序列号
    private long lastTimestamp = -1L; // 上次生成ID的时间戳

    public IdWorker(@Value("${idworker.worker-id}") long workerId,@Value("${idworker.datacenter-id}") long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("工作节点ID不能大于%d或小于0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("数据中心ID不能大于%d或小于0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    public synchronized long nextId() { // 生成下一个ID
        long timestamp = timeGen(); // 获取当前时间戳

        if (timestamp < lastTimestamp) { // 如果当前时间小于上次时间戳，则抛出异常
            throw new RuntimeException(String.format("系统时钟回拨。拒绝在%d毫秒内生成新的ID", lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) { // 当前时间与上次时间相同，序列号加一
            sequence = (sequence + 1) & sequenceMask;
            if (sequence == 0) { // 如果序列号为0，则等待下一毫秒
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else { // 时间不同，重置序列号为0
            sequence = 0L;
        }

        lastTimestamp = timestamp; // 更新上次时间戳

        return ((timestamp - twepoch) << timestampLeftShift) | // 根据Twitter的Snowflake算法计算ID
                (datacenterId << datacenterIdShift) |
                (workerId << workerIdShift) |
                sequence;
    }

    protected long tilNextMillis(long lastTimestamp) { // 等待下一毫秒
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    protected long timeGen() { // 获取当前时间戳（毫秒）
        return System.currentTimeMillis();
    }
}
