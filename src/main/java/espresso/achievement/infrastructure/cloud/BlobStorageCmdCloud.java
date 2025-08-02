package espresso.achievement.infrastructure.cloud;

import java.time.OffsetDateTime;


public class BlobStorageCmdCloud {

    private final String accountName = "<account-name>";
    private final String accountKey = "<account-key>";
/*
    private BlobServiceClient blobServiceClient;

    public BlobStorageCmdCloud() {

        StorageSharedKeyCredential credential = new StorageSharedKeyCredential(accountName, accountKey);

        blobServiceClient = new BlobServiceClientBuilder()
                .endpoint(String.format("https://%s.blob.core.windows.net/", accountName))
                .credential(credential)
                .buildClient();
    }

    public String createServiceSASContainer(BlobContainerClient containerClient) {
        // Create a SAS token that's valid for 1 day, as an example
        OffsetDateTime expiryTime = OffsetDateTime.now().plusDays(1);

        // Assign read permissions to the SAS token
        BlobContainerSasPermission sasPermission = new BlobContainerSasPermission()
                .setReadPermission(true)
                .setWritePermission(true)
                .setCreatePermission(true);

        BlobServiceSasSignatureValues sasSignatureValues = new BlobServiceSasSignatureValues(expiryTime, sasPermission)
                .setStartTime(OffsetDateTime.now().minusMinutes(5));

        String sasToken = containerClient.generateSas(sasSignatureValues);
        return sasToken;
    }
*/
}
