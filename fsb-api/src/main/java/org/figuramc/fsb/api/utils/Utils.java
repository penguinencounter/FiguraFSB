package org.figuramc.fsb.api.utils;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

public class Utils {
    private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3',
            '4', '5', '6', '7',
            '8', '9', 'a', 'b',
            'c', 'd', 'e', 'f'
    };

    public static String hexFromBytes(byte[] arr, boolean reverse) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            byte b = arr[i];
            char c1 = HEX_CHARS[(b >> 4) & 0xF];
            char c2 = HEX_CHARS[b & 0xF];
            if (reverse) {
                sb.insert(0, c2);
                sb.insert(0, c1);
            } else {
                sb.append(c1);
                sb.append(c2);
            }
        }
        return sb.toString();
    }

    public static String hexFromBytes(byte[] arr) {
        return hexFromBytes(arr, false);
    }

    public static String uuidToHex(UUID uuid) {
        return hexFromBytes(toBytes(uuid.getMostSignificantBits())) + hexFromBytes(toBytes(uuid.getLeastSignificantBits()));
    }

    private static int fromHexChar(char c) {
        switch (c) {
            case '0':
                return 0x0;
            case '1':
                return 0x1;
            case '2':
                return 0x2;
            case '3':
                return 0x3;
            case '4':
                return 0x4;
            case '5':
                return 0x5;
            case '6':
                return 0x6;
            case '7':
                return 0x7;
            case '8':
                return 0x8;
            case '9':
                return 0x9;
            case 'a':
                return 0xa;
            case 'b':
                return 0xb;
            case 'c':
                return 0xc;
            case 'd':
                return 0xd;
            case 'e':
                return 0xe;
            case 'f':
                return 0xf;
            default:
                throw new IllegalStateException("Unexpected value: " + c);
        }
    }

    public static byte[] bytesFromHex(String hex) {
        return bytesFromHex(hex, false);
    }

    public static byte[] bytesFromHex(String hex, boolean reverse) {
        byte[] data = new byte[(int) Math.ceil(hex.length() / 2f)];
        int curShift = 0;
        if (!reverse) {
            for (int i = 0; i < hex.length(); i++) {
                char c = hex.charAt(i);
                int v = fromHexChar(c);
                data[i / 2] |= (byte) (v << (4 - curShift));
                curShift = (curShift + 4) % 8;
            }
        } else {
            for (int i = hex.length() - 1; i >= 0; i--) {
                char c = hex.charAt(i);
                int v = fromHexChar(c);
                data[data.length - ((i / 2) + 1)] |= (byte) (v << (4 - curShift));
                curShift = (curShift + 4) % 8;
            }
        }
        return data;
    }

    public static byte[] toBytes(int i) {
        return new byte[]{
                (byte) ((i >> 24) & 0xFF),
                (byte) ((i >> 16) & 0xFF),
                (byte) ((i >> 8) & 0xFF),
                (byte) (i & 0xFF),
        };
    }

    public static byte[] toBytes(long l) {
        return new byte[]{
                (byte) ((l >> 56) & 0xFF),
                (byte) ((l >> 48) & 0xFF),
                (byte) ((l >> 40) & 0xFF),
                (byte) ((l >> 32) & 0xFF),
                (byte) ((l >> 24) & 0xFF),
                (byte) ((l >> 16) & 0xFF),
                (byte) ((l >> 8) & 0xFF),
                (byte) (l & 0xFF)
        };
    }

    public static Hash getHash(byte[] data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return new Hash(digest.digest(data));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static Hash parseHash(String hash) {
        byte[] h = bytesFromHex(hash);
        return new Hash(h);
    }

    public static byte[] copyBytes(byte[] source) {
        return Arrays.copyOf(source, source.length);
    }

    public static String readStreamToString(FileInputStream fis, Charset charset) throws IOException {
        return new String(toByteArray(fis), charset);
    }

    public static byte[] toByteArray(FileInputStream fis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }
}
