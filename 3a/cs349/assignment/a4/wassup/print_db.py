#!/usr/bin/python
import json
import wassup


def print_db():
    app_db = wassup.app_db_load_from_file()
    print json.dumps(app_db, indent=4, sort_keys=True)


if __name__ == '__main__':
    print_db()
