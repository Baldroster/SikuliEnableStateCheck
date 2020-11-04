import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.sikuli.script.*;
import org.sikuli.script.Image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindUtil {


    static public boolean debugMode = false;

    /**
     * Search for an image in a region with a color matching check
     *
     * @param region we are looking for target
     * @param target what we are looking for
     * @return found region
     * @throws FindFailed
     * @throws IOException
     */
    public Region findWithColorSensitive(Region region, Pattern target) throws IOException, FindFailed {
        return findWithColorSensitive(region, target, null);
    }

    /**
     * Search for an image in a region with a color matching check
     *
     * @param region we are looking for target
     * @param target what we are looking for
     * @param mask   for target
     * @return found region
     * @throws FindFailed
     * @throws IOException
     */
    public Region findWithColorSensitive(Region region, Pattern target, Pattern mask) throws FindFailed, IOException {
        Image img = Element.getImageFromTarget(target);
        BufferedImage bMask = null;
        if (mask != null) {
            bMask = mask.getBImage();
            target.mask(mask);
        }
        region = region.find(target);
        region = new Region(region.getRect());
        BufferedImage regImg = region.getImage().get();
        Mat regImgMat = bImgToMat(regImg);
        saveImg(regImgMat);
        double similarity = target.getSimilar();
        List<Integer> listAvgWhat = getAvgRgb(img.get(), bMask);
        List<Integer> listAvgResult = getAvgRgb(regImg, bMask);
        for (int i = 0; i < listAvgResult.size(); i++) {
            if ((listAvgResult.get(i) < listAvgWhat.get(i) ? ((double) listAvgResult.get(i)) / listAvgWhat.get(i) : ((double) listAvgWhat.get(i)) / listAvgResult.get(i)) < similarity) {
                throw new FindFailed(FindFailed.createErrorMessage(region, img));
            }
        }
        return region;
    }

    /**
     * Convert BufferedImage to Mat
     *
     * @param image convertible BufferedImage
     * @return converted Mat image
     * @throws IOException
     */
    public static Mat bImgToMat(BufferedImage image) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();
        return Imgcodecs.imdecode(new MatOfByte(byteArrayOutputStream.toByteArray()), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
    }

    /**
     * SaveImage if debugMode on
     *
     * @param img
     */
    private static void saveImg(Mat img) {
        if (debugMode) {
            Imgcodecs.imwrite("C:\\sikuli\\res.png", img);
        }
    }

    /**
     * Get average pixel color for each RGB chanel on image
     *
     * @param bImg processed image
     * @param mask processed image mask
     * @return List with channel's average color
     */
    protected List<Integer> getAvgRgb(BufferedImage bImg, BufferedImage mask) {
        List<Integer> list = new ArrayList<>();
        int rgb, red, green, blue = 0;
        int rsum = 0;
        int gsum = 0;
        int bsum = 0;
        int maskedSum = 0;
        int imgH = bImg.getHeight();
        int imgW = bImg.getWidth();
        if ((mask != null) && !((imgW == mask.getWidth()) && (imgH == mask.getHeight()))) {
            throw new MaskAndImageSizeNotEqualsException(mask.getHeight(), mask.getWidth(), imgH, imgW);
        }
        for (int i = 0; i < imgW; i++) {
            for (int j = 0; j < imgH; j++) {
                if ((mask != null) && (((mask.getRGB(i, j) & 0x00ff0000) >> 16) == 0)
                        && (((mask.getRGB(i, j) & 0x0000ff00) >> 8) == 0)
                        && ((mask.getRGB(i, j) & 0x000000ff) == 0)) {
                    maskedSum += 1;
                } else {
                    rgb = bImg.getRGB(i, j);
                    red = (rgb & 0x00ff0000) >> 16;
                    green = (rgb & 0x0000ff00) >> 8;
                    blue = rgb & 0x000000ff;
                    rsum = red + rsum;
                    gsum = green + gsum;
                    bsum = blue + bsum; //calculate total blue value
                }
            }
        }
        list.add(rsum / (imgH * imgW - maskedSum));
        list.add(gsum / (imgH * imgW - maskedSum));
        list.add(bsum / (imgH * imgW - maskedSum));
        return list;
    }

    static class MaskAndImageSizeNotEqualsException extends RuntimeException {
        MaskAndImageSizeNotEqualsException(int maskH, int maskW, int imgH, int imgW) {
            super("Image size " + imgW + "*" + imgH + " not equals to mask size " + maskW + "*" + maskH);
        }
    }
}
