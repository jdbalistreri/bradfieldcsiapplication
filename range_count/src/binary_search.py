LEFT = "left"
RIGHT = "right"

def range_count_binary_search(a, x):
    left_edge = find_edge_of_range(a, x, LEFT)
    right_edge = find_edge_of_range(a, x, RIGHT)

    if left_edge == -1 or right_edge == -1:
        return 0

    return right_edge - left_edge + 1


def find_edge_of_range(a, x, edge):
    start = 0
    end = len(a)

    while start < end:
        i = (start + end) // 2
        found = a[i]
        if found == x:
            if edge == LEFT:
                if i == 0 or a[i-1] != x:
                    return i
                end = i
            elif edge == RIGHT:
                if i == len(a) - 1 or a[i+1] != x:
                    return i
                start = i + 1
            else:
                raise ValueError("Invalid edge type")
        elif found < x:
            start = i + 1
        else:
            end = i

    return -1
