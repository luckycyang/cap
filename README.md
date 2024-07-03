# cap

Computer Architecture Project In Guet

## `Intro`

使用 `devenv` 配置的环境, `scala` 的版本实际由配置文件说明，默认的 `Java` 版本为 21

当前我对 `mill` 和 `sbt` 工具不太熟悉。当前使用 `helix` 编辑器。

安装 `bloop` 让 `Metals` 识别, 使用 `mill` 导入, 参考 [`Mill`](https://scalameta.org/metals/docs/build-tools/mill/)

```Bash
# 安装 bloop , metals 狂喜
mill --import "ivy:com.lihaoyi::mill-contrib-bloop:" mill.contrib.bloop.Bloop/install
```

**测试**


```Bash
# cap 代表项目名, object %NAME% extends SbtModule 的 %NAME% 就是了
mill cap.test
```

可以看到基本测试的波形，生成 `test_run_dir` 目录内对于存放波形文件和 `fir` 表示。可以采用 `gtkware` 或 其他可看 `Vcd` 波形的软件，这里推荐 `vscode` 插件[`Digtal IDE`](https://marketplace.visualstudio.com/items?itemName=sterben.fpga-support)


你可以在 `test` 分支看到下面波形的示例代码 `OperationTestSpec`

![image.png](https://s2.loli.net/2024/07/03/bJwlNYI2g8Mpyo5.png)

**运行与构建**

他两都行，`mill` 在多 `object` 时请在 `build.sc` 定义主入口 `mainClass`
