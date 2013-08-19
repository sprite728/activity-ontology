from SPARQLWrapper import SPARQLWrapper, JSON

def get_types(resource):
    select = 'SELECT ?type'
    where = 'WHERE { ' + resource + ' rdf:type ?type . }'
    query = "\n".join([select, where])

    sparql = SPARQLWrapper("http://dbpedia.org/sparql")
    sparql.setQuery(query)
    sparql.setReturnFormat(JSON)

    types = []
    try:
        results = sparql.query().convert()
        for result in results['results']['bindings']:
            types.append(result['type']['value'])
    except:
        pass

    return types

def count_matched(resources):
    owl = 'http://www.w3.org/2002/07/owl#Thing'
    none, owl_thing, other = [], [], []

    for resource in resources:
        http = '<http://dbpedia.org/resource/' + resource.replace(' ',
                '_').capitalize() + '>'

        types = get_types(http)
        if not types:
            none.append(resource)
        elif not filter(lambda s: s != owl, types):
            owl_thing.append(resource)
        else:
            other.append(resource)

    return none, owl_thing, other

def stats():
    with open('../results/parsed_results.csv') as f:
        objects = map(lambda line: line.split(',')[1], f.readlines())
        none, owl_thing, other = count_matched(set(objects))

        print '---DBPedia types'
        print 'Have no type associated: {0}'.format(len(none))
        print 'Have only owl:Thing: {0}'.format(len(owl_thing))
        print 'Have other types: {0}'.format(len(other))

stats()
