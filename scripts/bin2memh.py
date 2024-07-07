import sys


def bin_to_readmemh(input_file, output_file):
    # 打开二进制文件进行读取
    with open(input_file, "rb") as f:
        binary_data = f.read()

    # 确保二进制数据的长度是4字节的倍数
    if len(binary_data) % 4 != 0:
        raise ValueError("二进制文件的长度必须是4字节的倍数")

    # 打开输出文件进行写入
    with open(output_file, "w") as f:
        for i in range(0, len(binary_data), 4):
            # 每次读取4个字节
            word = binary_data[i : i + 4]
            # 将4个字节转换为32位的十六进制数，并写入输出文件
            f.write(f"{int.from_bytes(word, byteorder='big'):08x}\n")


if __name__ == "__main__":
    if len(sys.argv) != 3:
        print("用法: python bin2memh.py <inputpath> <outputpath>")
        sys.exit(1)

    input_file = sys.argv[1]
    output_file = sys.argv[2]

    try:
        bin_to_readmemh(input_file, output_file)
        print(f"成功转换 {input_file} 为 {output_file}")
    except Exception as e:
        print(f"发生错误: {e}")
        sys.exit(1)
