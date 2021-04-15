#!/usr/bin/python
# -*- coding: UTF-8 -*-

import numpy as np

recursionNum = np.loadtxt("../bin/recursion-record.log")
recursionNum = np.average(recursionNum)
print("average recursion time:{}ms".format(recursionNum))
loopNum = np.loadtxt("../bin/loop-record.log")
loopNum = np.average(loopNum)
print("     average loop time:{}ms".format(loopNum))