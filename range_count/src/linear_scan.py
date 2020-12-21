def range_count_linear_scan(a, x):
    count = 0
    for v in a:
        if v == x:
            count += 1
        elif v > x:
            break
    return count
