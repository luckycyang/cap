import sys

def binary_to_verilog_memh(binary_file, output_file):
    with open(binary_file, 'rb') as bin_file:
        binary_data = bin_file.read()

    # 确保数据长度是4字节的倍数
    if len(binary_data) % 4 != 0:
        raise ValueError("二进制文件的长度不是4字节的倍数")

    with open(output_file, 'w') as verilog_file:
        for i in range(0, len(binary_data), 4):
            # 读取4字节，转化为小端序整型
            word = binary_data[i:i+4]
            # 转化为16进制字符串，前缀不带 '0x' 并补齐8位
            hex_str = ''.join(f'{b:02x}' for b in word[::-1])
            verilog_file.write(hex_str + '\n')

    print(f"转换完成，输出文件: {output_file}")

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("用法: python bin2hex.py <inputfile> <outputfile>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    try:
        binary_to_verilog_memh(input_file, output_file)
    except Exception as e:
        print(f"错误: {e}")
        sys.exit(1)
