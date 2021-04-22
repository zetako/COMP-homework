#!/usr/bin/python
# -*- coding: UTF-8 -*-

import sys
import random

leng = 10000
digSet = ['0','1','2','3','4','5','6','7','8','9']
opSet = ['+','-']

if (len(sys.argv) >= 2):
    if (sys.argv[1].isnumeric()):
        leng = int(sys.argv[1])
ret = random.choice(digSet)
while (leng > 0):
    ret = ret + random.choice(opSet) + random.choice(digSet)
    leng-=1
print(ret)