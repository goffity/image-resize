package com.goffity.utilties.image.resize;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

@SpringBootApplication
public class ImageResizeApplication {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(ImageResizeApplication.class, args);
        resizeImage(args[0]);
    }

    public static void resizeImage(String fileLocation) throws IOException {
        File rootDir = new File(fileLocation);

        if (rootDir.exists()) {
            File[] files = rootDir.listFiles();

            assert files != null;
            for (File file : files) {
                if (file.isFile() && isImage(file)) {
                    System.out.println(file.getName());
                    System.out.println(file.getAbsolutePath());

                    File tempFile = new File("a1" + FilenameUtils.getExtension(file.getName()));
                    FileUtils.copyFile(file, tempFile);
                    compressedImage(tempFile.getAbsolutePath(), file.getAbsolutePath(), true);
                } else if (file.isDirectory()) {
                    resizeImage(file.getAbsolutePath());
                }
            }
        }
    }

    public static boolean isImage(File file) {
        String extension = FilenameUtils.getExtension(file.getName());
        System.out.println(extension);
        return "jpg".equalsIgnoreCase(extension) || "png".equalsIgnoreCase(extension);
    }

    public static File compressedImage(String originalFile, String destinationCompressedFile, boolean deleteOriginal) throws IOException {
        System.out.println("compressedImage()");

        File original = new File(originalFile);
        BufferedImage bufferedImage = ImageIO.read(original);

        File imageCompressed = new File(destinationCompressedFile);
        OutputStream outputStream = new FileOutputStream(imageCompressed);

        ImageWriter imageWriter = ImageIO.getImageWritersByFormatName("jpg").next();

        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(outputStream);

        imageWriter.setOutput(imageOutputStream);

        ImageWriteParam imageWriteParam = imageWriter.getDefaultWriteParam();

        imageWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        imageWriteParam.setCompressionQuality(0.5f);

        imageWriter.write(null, new IIOImage(bufferedImage, null, null), imageWriteParam);

        outputStream.close();
        imageOutputStream.close();
        imageWriter.dispose();

        if (deleteOriginal) {
            FileUtils.deleteQuietly(original);
        }

        return imageCompressed;
    }
}
