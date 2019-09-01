# Cafebabe Lite

Cafebabe is an user-friendly java bytecode editor suited for bytecode amateurs and pros. It supports multiview; edit and decompile multiple class files at once. Many features have been adopted from JByteMod, including the control flow graphing capability. Using Cafebabe you don't have to create or edit stack frames manually, they will automatically get regenerated at exporting, without the requirement of library jar files needed at runtime. Method descriptors will automatically be reversed to original java-syntax and node access can be changed without knowing the actual jvm access-values.
Try catch blocks are (*going to be*) automatically implemented into the bytecode as well as jump offsets (labels). You don't need to know every opcode, the in-editor help will explain them to you.
Note that this version is not completed yet and may not support some features listed here. If you encounter bugs, please report them by opening an issue. You are also welcome to contribute to this project!

## Screenshots
![Screenshot 1](https://i.imgur.com/hR5TUUE.png)
![Screenshot 2](https://i.imgur.com/GHtHY6v.png)
![Screenshot 3](https://i.imgur.com/yU0BvIs.png)
![Screenshot 4](https://i.imgur.com/GAVzrhy.png)
![Screenshot 5](https://i.imgur.com/XCja3oY.png)
## Libraries
- Objectweb ASM 7.0 (Modified version)
- WebLaF 1.28
- RSyntaxTextArea ?
- CFR Decompiler 0.145
- JGraphX ?

    
