def calculate_firsts(grammar):
    first = {}
    terminals = set()

    # print(terminals)

    def calculate_first(rules, simbolo, primeros):
        if simbolo in terminals:
            return {simbolo}
        if simbolo in primeros:
            return primeros[simbolo]

        conjunto_primero = set()
        for rule in rules[simbolo]:
            for i in range(len(rules)):
                if rule[i].isupper():
                    conjunto_primero |= calculate_first(rules, rule[i], primeros)
                    if 'e' not in calculate_first(rules, rule[i], primeros):
                        break
                else:
                    conjunto_primero.add(rule[i])
                    break
            else:
                conjunto_primero.add('e')

        primeros[simbolo] = conjunto_primero
       # print("producciones: ", rules, "conjunto_primeros: ", conjunto_primero)
        return conjunto_primero

    for no_terminal in grammar:
        terminals |= {s for prod in grammar[no_terminal] for s in prod if s.islower() or s == 'e'}
        calculate_first(grammar, no_terminal, first)

    #print("first: ", first)

    return first, terminals


def calculate_follows(grammar, primeros):
    follows = {simbolo: set() for simbolo in grammar}
    follows[list(grammar.keys())[0]].add('$')

    for no_terminal in grammar:
        for produccion in grammar[no_terminal]:
            for i in range(len(produccion)):
                simbolo = produccion[i]
                if simbolo.isupper():
                    siguiente_idx = i + 1
                    while siguiente_idx < len(produccion):
                        follows[simbolo] |= primeros.get(produccion[siguiente_idx], set()) - {'e'}
                        if 'e' not in primeros.get(produccion[siguiente_idx], set()):
                            break
                        siguiente_idx += 1
                    else:
                        follows[simbolo] |= follows[no_terminal]

                    if siguiente_idx == len(produccion):
                        follows[simbolo] |= follows[no_terminal]
    #print("follows ", follows)
    return follows


def es_ll1(grammar):
    firts, terminales = calculate_firsts(grammar)
    follow = calculate_follows(grammar, firts)

    for no_terminal in grammar:
        for rule in grammar[no_terminal]:
            primeros = firts[no_terminal]
            for siguiente in follow[no_terminal]:
                if siguiente in primeros:
                    return "Error"

            if 'e' in primeros:
                for siguiente in follow[no_terminal]:
                    if siguiente in firts[no_terminal]:
                        return "Error"

    return "cierto"
