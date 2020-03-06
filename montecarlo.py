import random
import matplotlib.pyplot as plt

sampleSize = 100

startingFunds = 1000
wagerSize = 100
wagerCount = 1000


def rollDice():
    roll = random.randint(1,100)

    if roll == 100:
        return False
    elif roll <= 50:
        return False
    elif 100 > roll >= 51:
        return True

def simplebettor(funds, initial_wager, wager_count):
    value = funds
    wager = initial_wager

    wX = []

    vY = []

    currentWager = 1

    while currentWager <= wager_count:
        if rollDice():
            value += wager
            wX.append(currentWager)
            vY.append(value)

        else:
            value -= wager
            wX.append(currentWager)
            vY.append(value)

        currentWager += 1

    plt.plot(wX,vY, 'g')

x = 0


def doubler_bettor(funds,initial_wager,wager_count):

    value = funds
    wager = initial_wager
    wX = []
    vY = []
    currentWager = 1
    previousWager = 'win'
    previousWagerAmount = initial_wager

    while currentWager <= wager_count:
        if previousWager == 'win':
            if rollDice():
                value += wager
                wX.append(currentWager)
                vY.append(value)
            else:
                value -= wager 
                previousWager = 'loss'
                previousWagerAmount = wager
                wX.append(currentWager)
                vY.append(value)
                if value < 0:
                    currentWager += 10
        elif previousWager == 'loss':
            if rollDice():
                wager = previousWagerAmount * 2
                value += wager
                wager = initial_wager
                previousWager = 'win'
                wX.append(currentWager)
                vY.append(value)
            else:
                wager = previousWagerAmount * 2
                value -= wager
                previousWager = 'loss'
                previousWagerAmount = wager
                wX.append(currentWager)
                vY.append(value)
                if value < 0:
                    currentWager += 10

        currentWager += 1
    plt.plot(wX,vY,'c')
    
while x <= 10:
    simplebettor(100,20,100)
    doubler_bettor(100,20,100)
    x+=1
plt.axhline(0, color = 'r')
plt.ylabel("Bettor's Funds")
plt.xlabel("Wager Count")
plt.show()
