#! /usr/bin/python2.7
# -*- coding: utf-8 -*-
# 监控磁盘io和网络io生成csv文件：disk_io.csv 和 net_io.csv

import psutil
import time
import sys


def get_monitor(seconds=10):
    """获取监控数据：
        磁盘io sdiskio(
            read_count=487914, 读次数
            write_count=27717916, 写次数
            read_bytes=85336393728, 读字节数
            write_bytes=1306446049280, 写字节数
            read_time=845507, 从磁盘读取花费的时间
            write_time=333893415, 向磁盘写入花费的时间
            read_merged_count=172, 读合并次数
            write_merged_count=838948, 写合并次数
            busy_time=3564888 I/O本身花费的时间
        )
        网络io snetio(
            bytes_sent=1249542885131, 发送字节数
            bytes_recv=1123413227097, 接收字节数
            packets_sent=3161938288,  发送包数
            packets_recv=3155701397,  接收包数
            errin=0, errout=0, dropin=0, dropout=0
        )
        cpu
    """
    disk_io_list = []
    net_io_list = []
    for i in range(seconds):
        # 采集时间
        s_time = time.strftime('%H:%M:%S', time.localtime(time.time()))

        # 磁盘io
        disk_io = list(psutil.disk_io_counters())
        s_disk_io = [str(i) for i in disk_io]
        s_disk_io.append(s_time)
        disk_io_list.append(','.join(s_disk_io) + '\n')

        # 网络io
        net_io = list(psutil.net_io_counters())
        s_net_io = [str(i) for i in net_io]
        s_net_io.append(s_time)
        net_io_list.append(','.join(s_net_io) + '\n')
        time.sleep(1)

    # 记录磁盘io
    with open('disk_io.csv', 'w') as f:
        f.write('read_count,write_count,read_bytes,write_bytes,read_time,write_time,read_merged_count,write_merged_count,busy_time,time\n')
        f.writelines(disk_io_list)

    # 记录网络io
    # 以 'w' 方式打开，每次均覆盖原文件，注意备份
    with open('net_io.csv', 'w') as f:
        f.write(
            'bytes_sent,bytes_recv,packets_sent,packets_recv,errin,errout,dropin,dropout,time\n')
        f.writelines(net_io_list)


if __name__ == '__main__':

    if len(sys.argv) < 2:
        print 'Usage: python monitor.py <seconds>'
    else:
        get_monitor(int(sys.argv[1]))
        print 'Complete!'
