import sys
import random
n = int(sys.argv[1])
msg = ''
msg += str(n) + ' ' + str(n) + '\n'
msg += '\n'.join(str(i) for i in range(n)) + '\n'
msg += ' '.join(str(i) for i in range(n)) + '\n'

m = list(range(n))
for _ in range(2 * n):
    random.shuffle(m)
    msg += ' '.join(str(i) for i in m) + '\n'
open(sys.argv[2],'w').write(msg)