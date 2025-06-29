package espresso.achievement.infrastructure.repositories;

import java.sql.Blob;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


import espresso.achievement.domain.entities.KeyGenerator;
import espresso.achievement.domain.readModels.MediaStorageDetailReadModel;
import espresso.achievement.service.AppConfiguration;

@Component
public class MediaQryBlobDBProvider {

    @Autowired
    private AppConfiguration appConfiguration;

    public MediaStorageDetailReadModel getMediaStorageDetail(String key, String userkey) {

        MediaStorageDetailReadModel entity = new MediaStorageDetailReadModel(
                appConfiguration.getCloudConfig().getStorageUrl(),
                "media-files",
                "/pre-media/" + key + "/uploaded",
                KeyGenerator.generateKeyString(24));

        return entity;

    }

    /*
     * private String createServiceSASContainer(BlobContainerClient containerClient)
     * {
     * String sasToken = "";
     * 
     * // Create a SAS token that's valid for 1 day, as an example
     * OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);
     * 
     * // Assign read permissions to the SAS token
     * BlobContainerSasPermission sasPermission = new BlobContainerSasPermission()
     * .setReadPermission(true)
     * .setCreatePermission(true)
     * .setWritePermission(true);
     * 
     * BlobServiceSasSignatureValues sasValues = new
     * BlobServiceSasSignatureValues(expiryTime, sasPermission)
     * .setStartTime(OffsetDateTime.now());
     * 
     * sasToken = containerClient.generateSas(sasValues);
     * 
     * return sasToken;
     * }
     */
}