# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.
import collections
from grammar_analyzer import es_ll1
from chain_analyzer import analyzer

def constructor(archive_code):
    mkn = archive_code.readline().split()
    # print(mkn)

    no_terminals = []
    rules = collections.defaultdict(list)
    chains = list(range(int(mkn[2])))

    # print(len(no_terminals), len(chains))

    #print(mkn[0])
    line = archive_code.readline().split()
    #print(line)
    no_terminals.extend(line)

    for i in range(int(mkn[1])):
        line = archive_code.readline()
        line = line.strip()
        key , value = line.split("-")
        rules[key].append(value)


    for i in range(len(chains)):
        line = archive_code.readline().split()
        chains[i] = line[0]

    return no_terminals, rules, chains

def main():

    archive_code = open("code.txt", "r")
    n = int(archive_code.readline())

    no_terminals_list = []
    rules_list = []
    chains_list = []

    for i in range(n):

        no_terminals, rules, chains = constructor(archive_code)
        no_terminals_list.append(no_terminals)
        rules_list.append(rules)
        chains_list.append(chains)

        print(f"No terminales {i + 1} son:", no_terminals)
        print(f"Las reglas {i + 1} son:", rules)
        print(f"Las cadenas {i + 1} son:", chains)
        print()

    archive_code.close()

    for idx, rule_dict in enumerate(rules_list):
        print(f"Análisis LL(1) para el diccionario rules {idx + 1}:")
        result = es_ll1(rule_dict)
        print(f"Resultado para el diccionario rules {idx + 1}: {result}")
        if result == "cierto":
            analyzer(no_terminals_list[idx], rules_list[idx],chains_list[idx])
        print()

if __name__ == '__main__':
    main()


# See PyCharm help at https://www.jetbrains.com/help/pycharm/
# Press the green button in the gutter to run the script.

