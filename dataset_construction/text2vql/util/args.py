import argparse

def makeParser():    
    # Configure arguments
    parser = argparse.ArgumentParser(description='Parse dataset')

    parser.add_argument('--metamodels_datasets', type=str, default='metamodels/0-raw/ecore555,metamodels/0-raw/repo-atlanmod,metamodels/0-raw/repo-ecore-all,test_metamodel',
                        help='metamodels dataset folder (coma separated list)')
    parser.add_argument('--db', type=str, default='dataset.db', help='database file')
    parser.add_argument('--schema', type=str, default='schema.sql', help='SQL file to create databse tables')
    parser.add_argument('--sampleloc', type=str, default='metamodels/1-sample/', help='Target location of selected ecore metamodels for ruther processing')
    parser.add_argument('--jars', type=str, default='metamodels/2-jars/', help='SQL file to create databse tables')
    return parser
    
parser = makeParser()
