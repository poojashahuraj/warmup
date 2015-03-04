import uuid
import httplib
import boto
from boto.s3.connection import OrdinaryCallingFormat
from boto.s3.key import Key


def execute():
    connection = boto.connect_s3(aws_access_key_id="admin",
                                 aws_secret_access_key="f8e9b6dad5180397d506e4a8bbc7e1f33dbcc3ca7cef15030741b3dcfa326d3e559b220f80106a3e49df7076498ca20557f57f8cf2b90a73c9cbfcc887a8eb2b",
                                 host="us-east.formationds.com",
                                 port=8443,
                                 is_secure=1,
                                 calling_format=boto.s3.connection.OrdinaryCallingFormat())
    connection.create_bucket("acldemo")
    bucket = connection.get_bucket('acldemo')
    #k = bucket.new_key('panda')
    #k.set_contents_from_string('Hello, world!')


if __name__ == '__main__':
    execute()