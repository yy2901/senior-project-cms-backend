ssh -i /Users/yuhao/Documents/LightsailDefaultKey-us-east-1.pem ec2-user@18.208.219.144 'bash -s' <<'ENDSSH'
FILE=cms-backend-1.0-SNAPSHOT/RUNNING_PID
if test -f "$FILE"; then
    kill $(cat cms-backend-1.0-SNAPSHOT/RUNNING_PID)
    rm cms-backend-1.0-SNAPSHOT/RUNNING_PID
fi
nohup cms-backend-1.0-SNAPSHOT/bin/cms-backend -Dplay.http.secret.key=ad31779d4ee49d5ad5162bf1429c32e2e9933f3b
ENDSSH