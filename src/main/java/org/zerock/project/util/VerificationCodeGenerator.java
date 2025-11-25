package org.zerock.project.util;

import java.util.Random;

public class VerificationCodeGenerator {
    private static final String CHARACTERS = "0123456789";
    private static final Random random = new Random();

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}