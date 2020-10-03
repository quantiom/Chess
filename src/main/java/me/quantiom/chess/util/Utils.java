package me.quantiom.chess.util;

import org.lwjgl.BufferUtils;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.glfwGetCursorPos;

public class Utils {
    public static Vec2i getMousePos(long window) {
        DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);

        glfwGetCursorPos(window, xBuffer, yBuffer);

        double x = xBuffer.get(0);
        double y = yBuffer.get(0);

        return new Vec2i((int) x, (int) y);
    }
}
