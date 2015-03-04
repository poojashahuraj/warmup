__author__ = 'parallels'
import uuid
import httplib
import boto
import requests
from boto.s3.connection import OrdinaryCallingFormat
from boto.s3.key import Key


def __init__(self, user, password, host, port, secure, validate):
    print("init")
    self.user = user
    self.password = password
    self.host = host
    self.port = port
    self.secure = secure
    self.validate = validate


def get_user_token(user, password, host, port, secure, validate):
    print("in method")
    if secure:
        proto = 'https'
    else:
        proto = 'http'
    url = '%s://%s:%d/api/auth/token?login=%s&password=%s' % (proto, host, port, user, password)
    print("Getting credentials from: ", url)
    r = requests.get(url, verify=validate)
    rjson = r.json()
    print(rjson)
    return rjson['token']


def s3_connect(user_token):
    self.connection = boto.connect_s3(aws_access_key_id="admin",
                                      aws_secret_access_key=user_token,
                                      host="us-east.formationds.com",
                                      port=8443,
                                      is_secure=1,
                                      calling_format=boto.s3.connection.OrdinaryCallingFormat())
    log('S3 Connected!')

def main():
    print("main")
    user = "admin"
    password = "fds_pass"
    host = "us-east.formationds.com"
    port = 8443
    secure = "true"
    validate = "true"
    user_token = get_user_token(user, password, host, port, secure, validate);
    s3_connect(user_token)


if __name__ == '__main__':
    print("name")
    main()
