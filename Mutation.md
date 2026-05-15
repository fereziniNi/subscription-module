| Classe | Linha | ID do mutante | Justificativa |
|--------|------:|--------------|---------------|
| Subscription | 68 | changePlan - changed conditional boundary | O mutante altera `>` para `>=` na comparacao entre planos, mas o caso de igualdade ja e tratado anteriormente pela validacao `if (this.planType == newPlanType)`, que lanca excecao e impede a execucao desse ramo. Portanto, a mutacao nao altera o comportamento do programa e e equivalente ao codigo original. |
