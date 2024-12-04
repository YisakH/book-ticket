


arr = {(1, 2), (2, 1), (0, 3)}

y, x = 3, 0

a = y - x
print([(i, j + a) for i, j in arr])
print(any([i == (j + a) for i, j in arr]))
a = y + x
print([(i, j + a) for i, j in arr])
print(any([i == (-j + a) for i, j in arr]))