package com.example.kreatora3.mydemoapplication;

/**
 * Created by kreatora3 on 6/1/2016.
 */
public class PrinterCommands {

    //public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33,(byte)255, 3};
    public static byte[] FEED_LINE = {27, 74, 0 };
    public static byte[] SELECT_CYRILLIC_CHARACTER_CODE_TABLE = {0x1B, 0x74, 0x11};
}
