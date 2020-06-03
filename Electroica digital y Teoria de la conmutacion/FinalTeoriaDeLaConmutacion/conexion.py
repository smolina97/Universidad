from ubidots import ApiClient

print("Program Started")

api = ApiClient(token ='BBFF-QqEBRqhTmEsSj6STt6bOgJoKelwO4z') #update token

my_temp = api.get_variable('5dd297b48683d522e9f2c5b0') #update variable ID


while True:
    new_value = my_temp.save_value({'value':10})

