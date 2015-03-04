__author__ = 'parallels'
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
    # pick a bucket name ("canary") hello
    # create bucket if it doesn't exist
    bucket_name = 'canary'
    bucket1 = connection.create_bucket(bucket_name)

    bucket = connection.get_bucket(bucket_name)
    if bucket:
        print('Bucket (%s) already exists' % bucket_name)
        # clean up contents of buckets
        for key in bucket.list():
            bucket.delete_key(key)
            print ("key %s deleted "%key)
        connection.delete_bucket(bucket_name)

    # PUT a small object
    bucket = connection.create_bucket(bucket_name)
    key = bucket.new_key("hello.txt")
    key.set_metadata("Content-Type", "txt")
    key.set_contents_from_string("hello world !")

    # GET the object, make sure size and metadata match
    for key in bucket.list():
        print "{name}\t{size}\t{modified}".format(
            name = key.name,
            size = key.size,
            modified = key.last_modified,
            )

    # PUT a larger object
    keyLarge = bucket.new_key("largeKey.txt")
    keyLarge.set_contents_from_filename('/home/parallels/test.txt')
    # GET the object, make sure size and metadata match




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


    # exit 0
    # if anything goes wrong, exit 42
