package cn.matcheasy.framework.utils;

/**
 * @class SnowflakeIdUtil
 * @author: wangjing
 * @date: 2020/11/5/0005
 * @desc: 雪花算法, 分布式 id 生成算法,核心思想: 使用一个 64 bit 的 long 型的数字作为全局唯一 id .
 *
 * 64 bit 的 long 型数字:
 *   第一个部分是 1  个 bit：固定0,因为生成的 id 都是正数。
 *   第二个部分是 41 个 bit：表示的是时间戳。
 *   第三个部分是 5  个 bit：表示的是数据中心ID。
 *   第四个部分是 5  个 bit：表示的是工作机器ID。
 *   第五个部分是 12 个 bit：表示的序号,就是某个数据中心某台工作机器上这一毫秒内同时生成的自增且不重复 id 。
 *
 * SnowFlake算法的优点：
 *  （1）高性能高可用: 生成时不依赖于数据库,完全在内存中生成。
 *  （2）容量大: 每秒中能生成数百万的自增ID。
 *  （3）ID自增: 存入数据库中,索引效率高。
 *
 * SnowFlake算法的缺点：
 *  （1）依赖与系统时间的一致性,如果系统时间被回调或者改变,可能会造成id冲突或者重复。
 */
public class SnowflakeIdUtil {

    /** 时间起始标记点(2进制41位),作为基准,一般取系统的最近时间,一旦确定不能变动 */
    private final long twepoch = 1610355796836L;

    /** 数据中心ID,2进制5位(0~31) */
    private long datacenterId;

    /** 工作机器ID,2进制5位(0~31) */
    private long workerId;

    /** 毫秒内生成的多个id的序列号,2进制12位(0~4095),某个数据中心某台工作机器上这一毫秒内同时生成的 id 的序号 */
    private long sequence = 0L;

    /** 数据中心ID所占的位数,5位 */
    private final long datacenterIdBits = 5L;

    /** 工作机器ID所占的位数,5位 */
    private final long workerIdBits = 5L;

    /** 序列所占的位数,12位 */
    private final long sequenceBits = 12L;

    /** 支持的最大数据中心ID，结果最多只能是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxDatacenterId = -1L ^ (-1L << datacenterIdBits);

    /** 支持的最大工作机器ID，结果最多只能是31 (这个移位算法可以很快的计算出几位二进制数所能表示的最大十进制数) */
    private final long maxWorkerId = -1L ^ (-1L << workerIdBits);

    /** 工作机器ID向左移12位 */
    private final long workerIdShift = sequenceBits;

    /** 数据中心ID向左移17位(12+5) */
    private final long datacenterIdShift = sequenceBits + workerIdBits;

    /** 时间截向左移22位(12+5+5) */
    private final long timestampLeftShift = sequenceBits + workerIdBits + datacenterIdBits;

    /** 生成序列的掩码,这里为4095 (0b111111111111=0xfff=4095) */
    private final long sequenceMask = -1L ^ (-1L << sequenceBits);

    /** 上次生成ID的时间截,记录产生时间的毫秒数，判断是否是同1毫秒 */
    private long lastTimestamp = -1L;

    /**
     * 构造函数
     * @param workerId 工作机器ID (0~31)
     * @param datacenterId 数据中心ID (0~31)
     */
    public SnowflakeIdUtil(long workerId, long datacenterId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("worker Id can't be greater than %d or less than 0", maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenter Id can't be greater than %d or less than 0", maxDatacenterId));
        }
        this.workerId = workerId;
        this.datacenterId = datacenterId;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        //如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过这个时候应当抛出异常
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds", lastTimestamp - timestamp));
        }

        //如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & sequenceMask;
            //毫秒内序列溢出
            if (sequence == 0) {
                //阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            //时间戳改变，毫秒内序列重置
            sequence = 0L;
        }

        //上次生成ID的时间截
        lastTimestamp = timestamp;

        //移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - twepoch) << timestampLeftShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /** 测试 */
    public static void main(String[] args) {
        SnowflakeIdUtil idWorker = new SnowflakeIdUtil(1, 1);
        for (int i = 0; i < 1000; i++) {
            System.out.println(idWorker.nextId());
        }
    }

}
