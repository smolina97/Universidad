# This is a sample Python script.

# Press Shift+F10 to execute it or replace it with your code.cs.
# Press Double Shift to search everywhere for classes, files, tool windows, actions, and settings.

import re

archive_code = "code.cs"
constants = []


def save_constants(name):
    with open(name, "r") as f:
        text = f.read()
        for match in re.finditer(r'"(.+?)"'"|"'(?<!\\w)\\d+(?!\\w)', text):
            if match.group(1):
                constants.append(match.group(1))
            else:
                constants.append(match.group(0))
    #print(constants)
    return text

def lexeme_cons(text,list, number):
    words = set(list)
    for word in words:
        #print(word)
        regex = re.compile(r"\b" + word + r"\b")
        text = re.sub(regex, "reemplazo", text)
    #print(text)
    return text

def lexemes_id(text,list, number):
    words = set(list)
    for word in words:
        # print(word)
        regex = re.compile(r"\W" + word + r"\W")
        #print(regex)
        text = re.sub(regex, " " + str(number) + "'" + word + "' ", text)
    #print(text)
    return text

def tokenize(text, tokens):

    #print(tokens)

    for word, token in tokens.items():
        #print(word, token)
        regex = re.compile(r"\b" + re.escape(word) +r"\b")
        #regex = re.compile(re.escape(word))
        #print(regex)
        text = re.sub(regex,str(token),text)

    return text

def tokenize_operators(text, tokens):

    #print(tokens)

    for word, token in tokens.items():
        #print(word, token)
        regex = re.compile(re.escape(word))
        #print(regex)
        text = re.sub(regex,str(token),text)

    return text
def main():

    text = save_constants(archive_code)


    reserved_words = {"using":1 , "System":2 ,"Collections":3 ,"Generic":4 ,"Linq":5 ,
              "Text":6 , "Threading":7, "Tasks":8, "namespace":9, "Console":10 ,
              "Write":11 ,"WriteLine":12 , "ReadLine":13 , "Main":14 , "args":15 ,
              "class":16 , "private":17 , "public":18 , "static":19 , "void":20 ,
              "new":21 , "for":22 ,"Length":23 , "if":24 , "string":25 ,
              "int":26 , "Parse":27  }

    identifiers = ['PruebaVector16' , 'nombres', 'notas', 'Cargar', 'linea', 'notas', 'Ordenar' ,
                   'auxnota', 'auxnombre', 'Imprimir', 'pv', 'f', 'k']

    #constants:28 , identifiers:29

    operators = {".":30 ,";":31 ,"[":32 ,"]":33 ,"{":34 ,"}":35 ,"(":36 ,")":37 ,
               '"':38 ,":":39 ,"=":40, "+":41 ,"-":42,"<":43}

    constants_lexemes = lexemes_id(text, constants, 28)
    identifiers_lexemes = lexemes_id(constants_lexemes, identifiers, 29)
    tokenize_reserved_words = tokenize(identifiers_lexemes, reserved_words)
    tokenize_op = tokenize_operators(tokenize_reserved_words, operators)


    print(tokenize_op)

# Press the green button in the gutter to run the script.
if __name__ == '__main__':
   main()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
