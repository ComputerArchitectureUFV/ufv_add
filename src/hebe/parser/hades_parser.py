def get_hds_string(file_name):
    hds_file = open(file_name, 'r')
    return hds_file.read()


def get_name(hds_string):
    linhas = hds_string.split('\n')
    return linhas[2].split(' ')[1]

def get_components(hds_string):
    componentes = {}
    hds_string = hds_string.lower()
    linhas = hds_string.split('\n')

    idle = 0
    getcomp = 1
    getsignals = 2
    end = 3
    state = idle

    for l in linhas:
        if l == '[components]':
            state = getcomp
            continue
        elif l == '[end components]':
            state = idle
            continue
        elif l == '[signals]':
            state = getsignals
            continue
        elif l == '[end signals]':
            state = idle
            continue
        elif l == '[end]':
            state = end
            continue

        if state == getcomp:
            tokens = l.split(' ')
            temp = tokens[0].split('.')
            inst_name = tokens[1]
            try:
                data_width = int(tokens[6])
            except:
                data_width = 0
            op = temp[len(temp) - 1]
            if op == 'ipin' or op == 'clockgen':
                continue
            tipo = tokens[len(tokens) - 1]
            if tipo == 'i':
                const = tokens[7]
            elif tipo == 'n':
                const = tokens[len(tokens)-2]
            else:
                const = 0
            map_values = {'operator': 'hebe_'+op, 'type': tipo, 'data_width': data_width, 'const': const,
                          'con': []}
            componentes[inst_name] = map_values
        elif state == getsignals:
            tokens = l.split(' ')
            for c in componentes.keys():
                if (' %s ' % c) in l:
                    port = tokens[tokens.index(c) + 1]
                    wire = tokens[1]
                    if 'signalstdlogicvector' in l:
                        width = int(tokens[2])
                    else:
                        width = 1
                    if port == 'clk' or port == 'rst' or port == 'en':
                        wire = port
                    componentes[c]['con'].append([port, wire, width])
        elif state == end:
            break

    return componentes
