# https://artifacthub.io/packages/helm/bitnami/mongodb
# https://github.com/bitnami/charts/blob/main/bitnami/mongodb/values.yaml

helm install mongodb-liberica oci://registry-1.docker.io/bitnamicharts/mongodb --values helm-values.yaml

# MongoDB&reg; can be accessed on the following DNS name(s) and ports from within your cluster:
#     mongodb-liberica.default.svc.cluster.local

# To get the root password run:
#     export MONGODB_ROOT_PASSWORD=$(kubectl get secret --namespace default mongodb-liberica -o jsonpath="{.data.mongodb-root-password}" | base64 -d)

# To get the password for "pedro" run:
#     export MONGODB_PASSWORD=$(kubectl get secret --namespace default mongodb-liberica -o jsonpath="{.data.mongodb-passwords}" | base64 -d | awk -F',' '{print $1}')

# To connect to your database, create a MongoDB&reg; client container:
#     kubectl run --namespace default mongodb-liberica-client --rm --tty -i --restart='Never' --env="MONGODB_ROOT_PASSWORD=$MONGODB_ROOT_PASSWORD" --image docker.io/bitnami/mongodb:8.0.1-debian-12-r0 --command -- bash

# Then, run the following command:
#     mongosh admin --host "mongodb-liberica" --authenticationDatabase admin -u $MONGODB_ROOT_USER -p $MONGODB_ROOT_PASSWORD

# To connect to your database from outside the cluster execute the following commands:
#     kubectl port-forward --namespace default svc/mongodb-liberica 27017:27017 &
#     mongosh --host 127.0.0.1 --authenticationDatabase admin -p $MONGODB_ROOT_PASSWORD

# WARNING: There are "resources" sections in the chart not set. Using "resourcesPreset" is not recommended for production. For production installations, please set the following values according to your workload needs:
#   - arbiter.resources
#   - resources
# +info https://kubernetes.io/docs/concepts/configuration/manage-resources-containers/