//
// Created by BONNe
// Copyright - 2023
//


package lv.id.bonne.vaulthunters.serversync.utils;


import java.nio.charset.StandardCharsets;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * This is simple string compressor.
 */
public class Compress
{
    /**
     * Compress string into byte array.
     *
     * @param data the data
     * @return the byte array
     */
    public static byte[] compressString(String data)
    {
        byte[] input = data.getBytes();
        byte[] output = new byte[input.length];

        Deflater deflater = new Deflater();
        deflater.setInput(input);
        deflater.finish();
        deflater.deflate(output);

        return output;
    }


    /**
     * Decompress string.
     *
     * @param data the data
     * @return the string
     */
    public static String decompress(byte[] data)
    {
        try
        {
            byte[] result = new byte[data.length * 3];
            Inflater inflater = new Inflater();
            inflater.setInput(data);
            int resultLength = inflater.inflate(result);
            inflater.end();
            return new String(result, 0, resultLength, StandardCharsets.UTF_8);
        }
        catch (Exception e)
        {
            return "";
        }
    }
}
