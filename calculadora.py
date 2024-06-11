def adicionar(x, y):
    return x + y

def subtrair(x, y):
    return x - y

def multiplicar(x, y):
    return x * y

def dividir(x, y):
    if y == 0:
        return "Erro: Divisão por zero."
    else:
        return x / y

# Interface simples para o usuário escolher a operação e inserir os números
while True:
    print("Operações Disponíveis:")
    print("1. Adicionar")
    print("2. Subtrair")
    print("3. Multiplicar")
    print("4. Dividir")
    print("5. Sair")
    
    escolha = input("Escolha a operação (1/2/3/4/5): ")

    if escolha in ('1', '2', '3', '4'):
        num1 = float(input("Digite o primeiro número: "))
        num2 = float(input("Digite o segundo número: "))

        if escolha == '1':
            print("Resultado: ", adicionar(num1, num2))

        elif escolha == '2':
            print("Resultado: ", subtrair(num1, num2))

        elif escolha == '3':
            print("Resultado: ", multiplicar(num1, num2))

        elif escolha == '4':
            print("Resultado: ", dividir(num1, num2))
    
    elif escolha == '5':
        print("Saindo do programa.")
        break
    else:
        print("Entrada Inválida. Por favor escolha uma opção válida.")