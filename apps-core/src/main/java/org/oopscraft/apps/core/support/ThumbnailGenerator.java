package org.oopscraft.apps.core.support;

import lombok.extern.slf4j.Slf4j;
import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Method;
import org.imgscalr.Scalr.Mode;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

@Slf4j
public class ThumbnailGenerator {
	
	/**
	 * generate
	 * @param photoDataUrl
	 * @param width
	 * @param height
	 * @return
	 */
	public static String generate(String photoDataUrl, int width, int height) throws Exception {
		String thumbnailDataUrl = null;
		ByteArrayInputStream bais = null;
		ByteArrayOutputStream baos = null;
		try {
			String encodingPrefix = "base64,";
			int contentStartIndex = photoDataUrl.indexOf(encodingPrefix) + encodingPrefix.length();
			byte[] imageData = DatatypeConverter.parseBase64Binary(photoDataUrl.substring(contentStartIndex));
			bais = new ByteArrayInputStream(imageData);
			baos = new ByteArrayOutputStream();
			BufferedImage image = ImageIO.read(bais);
			BufferedImage thumbImg = Scalr.resize(image, Method.ULTRA_QUALITY, Mode.AUTOMATIC, width, height);
			ImageIO.write(thumbImg, "PNG", baos);
			byte[] encodeBase64 = Base64.getEncoder().encode(baos.toByteArray());
			String base64Encoded = new String(encodeBase64);
			thumbnailDataUrl = "data:image/png;base64,"+base64Encoded;
		}catch(Exception e) {
			log.warn(e.getMessage());
			throw e;
		}finally {
			if(bais != null) try { bais.close(); }catch(Exception ignore){}
			if(baos != null) try { baos.close(); }catch(Exception ignore){}
		}
		return thumbnailDataUrl;
	}
}
