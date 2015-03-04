import requests
import boto
from boto.s3.connection import OrdinaryCallingFormat
from boto.s3.key import Key


def get_user_token(user, password, host, port, secure):
    if secure:
        proto = 'https'
    else:
        proto = 'http'
    url = '%s://%s:%d/api/auth/token?login=%s&password=%s' % (proto, host, port, user, password)
    print("Getting credentials from: ", url)
    r = requests.get(url, verify=False)
    rjson = r.json()
    return rjson['token']


def execute(user_token, host, port, secure):
    connection = boto.connect_s3(aws_access_key_id="admin",
                                 aws_secret_access_key=user_token,
                                 host=host,
                                 port=port,
                                 is_secure=secure,
                                 calling_format=boto.s3.connection.OrdinaryCallingFormat())
    buckets = connection.get_all_buckets()
    for bucket in buckets:
        print(str(bucket))

    # pick a bucket name ("canary")
    # create bucket if it doesn't exist
    # clean up contents of buckets
    # PUT a small object
    # GET the object, make sure size and metadata match
    # PUT a larger object
    # GET the object, make sure size and metadata match
    # exit 0
    # if anything goes wrong, exit 42

def main():
    user = "admin"
    password = "formation1234"
    host = "us-east.formationds.com"
    omPort = 7443
    secure = "true"
    user_token = get_user_token(user, password, host, omPort, secure)
    execute(user_token, "us-east.formationds.com", 8443, 1)


if __name__ == '__main__':
    main()
