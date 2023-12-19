def parse(grammar, start_symbol, input_string):
    def parse_recursive(input_str, symbol):
        #print(input_str)
        if not input_str and not symbol:
            return True
        if not input_str or not symbol:
            return False

        next_symbol = symbol[0]

        if next_symbol in grammar:
            for rule in grammar[next_symbol]:
                if parse_recursive(input_str, rule + symbol[1:]):
                    return True

        if input_str[0] == next_symbol:
            #print(input_str)
            return parse_recursive(input_str[1:], symbol[1:])

        return False

    return parse_recursive(input_string, start_symbol)


def analyzer(no_terminals , rules , chains):

    for chain in chains:
        for non_terminal in no_terminals:
            if parse(rules, non_terminal, chain):

                print(f"'{chain}' si es una cadena válida para la gramatica.")
                break
        else:
            print(f"'{chain}' no es una cadena válida para la gramatica.")