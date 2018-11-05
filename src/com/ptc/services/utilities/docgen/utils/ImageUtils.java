/*
 *  Copyright:      Copyright 2018 (c) Parametric Technology GmbH
 *  Product:        PTC Integrity Lifecycle Manager
 *  Author:         Volker Eckardt, Principal Consultant ALM
 *  Purpose:        Custom Developed Code
 *  **************  File Version Details  **************
 *  Revision:       $Revision: 1.3 $
 *  Last changed:   $Date: 2018/05/18 02:18:19CET $
 */
package com.ptc.services.utilities.docgen.utils;

import com.mks.api.response.Field;
import com.ptc.services.utilities.docgen.Integrity;
import static com.ptc.services.utilities.docgen.utils.Logger.log;
import java.io.IOException;

//import sun.misc.BASE64Encoder;
//import sun.misc.BASE64Decoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author veckardt
 */
public class ImageUtils {

    public static void extractImage(Field fld, File imageFile) {
        try {
            String validFilename = imageFile.getAbsolutePath().replaceAll("\\*", "");
            ImageUtils.writeToFile(fld.getItem().getField("data").getBytes(), validFilename);
        } catch (IOException ex) {
            Logger.getLogger(Integrity.class.getName()).log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    /**
     * Decode string to image
     *
     * @param imageString The string to decode
     * @return decoded image
     */
    public static BufferedImage decodeToImage(String imageString) {

        BufferedImage image = null;
        byte[] imageByte;
        try {

            imageByte = Base64.decodeBase64(imageString);

            // BASE64Decoder decoder = new BASE64Decoder();
            // imageByte = decoder.decodeBuffer(imageString);
            ByteArrayInputStream bis = new ByteArrayInputStream(imageByte);
            image = ImageIO.read(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * Encode image to string
     *
     * @param image The image to encode
     * @param type jpeg, bmp, ...
     * @return encoded string
     */
    public static String encodeToString(BufferedImage image, String type) {
        String imageString = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(image, type, bos);
            byte[] imageBytes = bos.toByteArray();

            // String encodedString = Base64.encodeBase64String(fileContent);
            // BASE64Encoder encoder = new BASE64Encoder();
            // imageString = encoder.encode(imageBytes);
            imageString = Base64.encodeBase64String(imageBytes);

            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;
    }

    public static void writeToFile(byte[] imageBytes, String fileName) throws IOException {
        if (imageBytes != null) {
            // BASE64Encoder encoder = new BASE64Encoder();
            // String imageString = encoder.encode(imageBytes);
            String imageString = Base64.encodeBase64String(imageBytes);

            //
            // The mkdirs will create folder including any necessary but nonexistence
            // parent directories. This method returns true if and only if the directory
            // was created along with all necessary parent directories.
            //
            File file = new File(fileName);
            boolean result = new File(file.getParent()).mkdirs();
            // log("Status = " + result);

            BufferedImage newImg = decodeToImage(imageString);
            ImageIO.write(newImg, "png", file);
            log("  File " + fileName + " written.");
        }
    }

    public static void main(String args[]) throws IOException {
        /* Test image to string and string to image start */
        BufferedImage img = ImageIO.read(new File("files/img/TestImage.png"));
        BufferedImage newImg;
        String imgstr;
        imgstr = encodeToString(img, "png");
        log(imgstr);
        newImg = decodeToImage(imgstr);
        ImageIO.write(newImg, "png", new File("files/img/CopyOfTestImage.png"));
        /* Test image to string and string to image finish */
    }
}
