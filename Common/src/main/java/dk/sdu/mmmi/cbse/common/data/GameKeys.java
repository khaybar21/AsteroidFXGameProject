package dk.sdu.mmmi.cbse.common.data;

public class GameKeys {

    private static final int TOTAL_KEYS = 6;

    public static final int KEY_UP = 0;
    public static final int KEY_LEFT = 1;
    public static final int KEY_RIGHT = 2;
    public static final int KEY_SPACE = 3;
    public static final int KEY_DOWN = 4;
    public static final int KEY_X = 5;

    private static boolean[] currentKeyStates = new boolean[TOTAL_KEYS];
    private static boolean[] previousKeyStates = new boolean[TOTAL_KEYS];

    public GameKeys() {
        // Arrays are already statically initialized
    }

    public void updateKeyStates() {
        for (int i = 0; i < TOTAL_KEYS; i++) {
            previousKeyStates[i] = currentKeyStates[i];
        }
    }

    public void setKeyState(int keyCode, boolean isPressed) {
        currentKeyStates[keyCode] = isPressed;
    }

    public boolean isKeyHeld(int keyCode) {
        return currentKeyStates[keyCode];
    }

    public boolean isKeyJustPressed(int keyCode) {
        return currentKeyStates[keyCode] && !previousKeyStates[keyCode];
    }
}
