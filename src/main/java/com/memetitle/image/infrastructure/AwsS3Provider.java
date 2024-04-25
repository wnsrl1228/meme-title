package com.memetitle.image.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.memetitle.global.exception.StorageException;
import com.memetitle.image.dto.FileInfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.UUID;

import static com.memetitle.global.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AwsS3Provider {

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cdn.domain}")
    private String cdnDomain;


    private final AmazonS3 amazonS3;

    /**
     * AWS S3에 이미지 파일 업로드
     * @param multipartFile : 파일
     * @param dirName : 폴더 이름
     * @return : s3 url
     */
    public FileInfoResponse upload(final MultipartFile multipartFile, final String dirName){

        // 이미지 파일 유효성 체크 후 bufferedImage 반환
        validateImage(multipartFile);

        final String fileName = createFileName(multipartFile.getOriginalFilename(), dirName);

        // 메타데이터 지정, TODO : 필요여부 체크
        final ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(multipartFile.getSize());
        objectMetadata.setContentType(multipartFile.getContentType());

        // s3에 이미지 저장
        try(final InputStream inputStream = multipartFile.getInputStream()){
            amazonS3.putObject(new PutObjectRequest(bucket, fileName, inputStream, objectMetadata));
        } catch (IOException e){
            throw new StorageException(FAILED_TO_STORE_FILE);
        }

        // s3에 저장된 파일 url 얻어옴.
        final String path = amazonS3.getUrl(bucket, fileName).getPath();
        return FileInfoResponse.of(createUrlName(path));
    }

    public void delete(final String imageAddress){
        try{
            final String key = getKeyFromImageAddress(imageAddress);
            amazonS3.deleteObject(new DeleteObjectRequest(bucket, key));
        }catch (Exception e){
            throw new StorageException(FAILED_TO_DELETE_FILE);
        }
    }

    /**
     * 이미지 유효성 체크 후 BufferedImage 반환
     */
    public void validateImage(final MultipartFile multipartFile) {
        try {
            // 업로드된 파일의 BufferedImage 를 가져온다.
            final BufferedImage read = ImageIO.read(multipartFile.getInputStream());

            // 업로드된 파일이 이미지가 아닐 경우를 체크한다.
            if (read == null) {
                throw new StorageException(INVALID_FILE);
            }

            // 파일 확장자 체크 [jpeg, jpg, png, gif]
            if (!isValidImageExtension(multipartFile.getContentType())) {
                throw new StorageException(INVALID_FILE_EXTENSION);
            }
        } catch (IOException e) {
            throw new StorageException(INVALID_FILE);
        } catch (NullPointerException e) {
            throw new StorageException(INVALID_FILE);
        }
    }

    /**
     * cdn 호스트 붙인 url 반환
     * ex) https://dmpoeindaa9de.cloudfront.net + /post-image/filename.jpg
     */
    private String createUrlName(String path) {
        return cdnDomain + path;
    }

    private String createFileName(final String fileName, final String dirName){
        // 파일명을 다르게 하기 위해 UUID 를 붙임
        return dirName + "/" + UUID.randomUUID() + fileName;
    }

    private boolean isValidImageExtension(final String contentType) {
        // 유효한 이미지 확장자.
        return contentType.matches("^image/(jpeg|jpg|png|gif)$");
    }

    private String getKeyFromImageAddress(final String imageAddress) throws MalformedURLException, UnsupportedEncodingException {
        final URL url = new URL(imageAddress);
        final String decodingKey = URLDecoder.decode(url.getPath(), "UTF-8");
        return decodingKey.substring(1); // 맨 앞의 '/' 제거
    }
}