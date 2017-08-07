from hades_parser import get_name, get_components
from make_components import *


def make_dataflow(hds_string, data_width_ext):
    components = get_components(hds_string)
    name = get_name(hds_string)

    num_out = 0
    num_in = 0
    data_width = 0

    for c in components.keys():
        if 'out' in components[c]['operator']:
            num_out += int(components[c]['const'])
        if 'in' in components[c]['operator'] and components[c]['const'] != '':
            num_in += int(int(components[c]['const']))
        if components[c]['data_width'] != '':
            d = components[c]['data_width']
            if d > data_width:
                data_width = d

    m = Module(name)
    ports = {'clk': m.Input('clk'), 'rst': m.Input('rst'), 'start': m.Input('start'),
             'num_conf': m.Input('num_conf', 32),
             'num_data_in': m.Input('num_data_in', 32),
             'num_data_out': m.Input('num_data_out', 32),
             'available_write': m.Input('available_write'),
             'available_read': m.Input('available_read'),
             'req_rd_data': m.Output('req_rd_data'),
             'rd_data': m.Input('rd_data', data_width_ext),
             'req_wr_data': m.Output('req_wr_data'),
             'wr_data': m.Output('wr_data', data_width_ext), 'done': m.Output('done')
             }

    for c in components.keys():
        comp = components[c]
        for con in comp['con']:
            if con[1] not in ports.keys():
                ports[con[1]] = m.Wire(con[1], con[2])

    for c in components.keys():
        comp = components[c]
        n = comp['data_width']
        p = []
        for con in comp['con']:
            p.append((con[0], ports[con[1]]))

        p = sorted(p)
        exist = False
        if comp['operator'] in m.get_modules().keys():
            mkc = m.get_modules().get(comp['operator'])
            exist = True
        if comp['type'] == 'i':
            if not exist:
                if 'acc' in comp['operator']:
                    initvalue = 0
                    if 'min' in comp['operator']:
                        initvalue = 2 ** data_width - 1
                    elif 'max' in comp['operator']:
                        initvalue = 0
                    elif 'add' in comp['operator']:
                        initvalue = 0
                    elif 'mul':
                        initvalue = 1
                    else:
                        raise Exception('Componente não implementado!')

                    mkc = make_component_accumulator(comp['operator'], functions(comp['operator']), initvalue)
                elif 'histogram' in comp['operator']:
                    mkc = make_component_histogram(comp['operator'])
                else:
                    mkc = make_component_immediate(comp['operator'], functions(comp['operator']))
            params = [('N', int(n)), ('ID', int(comp['const']))]
            m.Instance(mkc, c, params, p)
        elif comp['type'] == 'b':
            if not exist:
                mkc = make_component_binary(comp['operator'], functions(comp['operator']))
            params = [('N', int(n))]
            m.Instance(mkc, c, params, p)
        elif comp['type'] == 'u':
            if not exist:
                mkc = make_component_unary(comp['operator'], functions(comp['operator']))
            params = [('N', int(n))]
            m.Instance(mkc, c, params, p)
        elif comp['type'] == 'n':
            params = []
            if not exist:

                if 'in' in comp['operator']:
                    p = p + [('start', ports['start']), ('num_data', ports['num_data_in']),
                             ('num_conf', ports['num_conf']), ('available_read', ports['available_read']),
                             ('req_rd_data', ports['req_rd_data']),
                             ('rd_data', ports['rd_data'])]
                    if num_in == 32:
                        mkc = make_component_in32(comp['operator'], num_in, data_width, data_width_ext)
                    else:
                        mkc = make_component_in(comp['operator'], num_in, data_width, data_width_ext)
                elif 'out' in comp['operator']:
                    p = p + [('start', ports['start']),
                             ('num_data', ports['num_data_out']), ('available_write', ports['available_write']),
                             ('req_wr_data', ports['req_wr_data']), ('wr_data', ports['wr_data']),
                             ('done', ports['done'])]
                    if num_in == 32:
                        mkc = make_component_out32(comp['operator'], num_out, data_width, data_width_ext)
                    else:
                        mkc = make_component_out(comp['operator'], num_out, data_width, data_width_ext)
            for pp in mkc.get_ports().keys():
                flag = True
                for np in p:
                    if np[0] == pp:
                        flag = False
                if flag:

                    p += [(pp, m.Wire(pp + '/*synthesis syn_keep=1*/', mkc.get_ports().get(pp).width))]
                    print('Warning: The port \'%s\' was not connected in design file. It was created and connected to a non-used wire!'%pp)
            m.Instance(mkc, c, params, p)

    return m
