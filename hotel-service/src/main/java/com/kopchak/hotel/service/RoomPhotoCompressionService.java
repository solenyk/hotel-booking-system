package com.kopchak.hotel.service;

import com.kopchak.hotel.domain.PhotoExtension;
import com.kopchak.hotel.domain.RoomPhoto;
import com.kopchak.hotel.dto.RoomPhotoDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

@Service
@Slf4j
public class RoomPhotoCompressionService {
    private final static String IMAGE_CONTENT_TYPE_PREFIX = "image/";

    public Set<RoomPhoto> convertMultipartFilesToPhotos(List<MultipartFile> multipartFiles) {
        Set<RoomPhoto> photos = new LinkedHashSet<>();
        if (multipartFiles != null) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (multipartFile != null) {
                    String fileName = multipartFile.getOriginalFilename();
                    if (isNonImageFile(multipartFile)) {
                        throw new RuntimeException(String.format("The file with name: %s must have an image type", fileName));
                    }
                    byte[] compressedImg = compressImage(multipartFile, fileName);
                    String generatedName = generateImageName(multipartFile);
                    if (multipartFile.getContentType() != null && Arrays.stream(PhotoExtension.values()).anyMatch(
                            ext -> multipartFile.getContentType().toUpperCase().contains(ext.name()))) {
                        String fileExtension = multipartFile.getContentType().replace(IMAGE_CONTENT_TYPE_PREFIX, "");
                        photos.add(RoomPhoto.builder()
                                .name(generatedName)
                                .type(PhotoExtension.valueOf(fileExtension.toUpperCase()))
                                .data(compressedImg)
                                .build());
                    }
                }
            }
        }
        return photos;
    }

    public Optional<RoomPhotoDTO> decompressImage(RoomPhoto photo) {
        return Optional.ofNullable(photo)
                .map(img -> {
                    try {
                        String imageName = img.getName();
                        byte[] decompressedImg = decompressImageData(img.getData());
                        log.info("The image with name: {} was successfully decompressed", imageName);
                        return Optional.of(new RoomPhotoDTO(imageName, img.getType(), decompressedImg));
                    } catch (IOException | DataFormatException e) {
                        log.error("Error decompressing the image with name: {}", img.getName(), e);
                        return Optional.<RoomPhotoDTO>empty();
                    }
                })
                .orElse(Optional.empty());
    }

    private boolean isNonImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType != null) {
            return !contentType.startsWith(IMAGE_CONTENT_TYPE_PREFIX);
        }
        return true;
    }

    private byte[] compressImage(MultipartFile multipartFile, String fileName) {
        try {
            byte[] data = multipartFile.getBytes();
            Deflater deflater = new Deflater();
            deflater.setLevel(Deflater.BEST_COMPRESSION);
            deflater.setInput(data);
            deflater.finish();

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length);
            byte[] compressedData = compressData(deflater, outputStream);
            outputStream.close();
            log.info("The image with name: {} was successfully compressed", fileName);
            return compressedData;
        } catch (IOException e) {
            String errorMsg = String.format("The image with name: %s cannot be compressed", fileName);
            log.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }
    }

    private byte[] compressData(Deflater deflater, ByteArrayOutputStream outputStream) throws IOException {
        byte[] tmp = new byte[4 * 1024];
        while (!deflater.finished()) {
            int size = deflater.deflate(tmp);
            outputStream.write(tmp, 0, size);
        }
        return outputStream.toByteArray();
    }

    private String generateImageName(MultipartFile multipartFile) {
        String fileExtension = ".".concat(multipartFile.getContentType()
                .replace(IMAGE_CONTENT_TYPE_PREFIX, ""));
        String randString = RandomStringUtils.randomAlphanumeric(4);
        String filename = multipartFile.getOriginalFilename();
        return filename == null ? randString.concat(fileExtension) :
                filename.replace(fileExtension, "").concat(randString).concat(fileExtension);
    }

    private byte[] decompressImageData(byte[] compressedData) throws IOException, DataFormatException {
        Inflater inflater = new Inflater();
        inflater.setInput(compressedData);

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream(compressedData.length)) {
            byte[] buffer = new byte[4 * 1024];

            while (!inflater.finished()) {
                int count = inflater.inflate(buffer);
                outputStream.write(buffer, 0, count);
            }
            return outputStream.toByteArray();
        }
    }
}