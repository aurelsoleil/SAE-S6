import requests

BASE_URL = "http://localhost:8045"

def wait():
    input("\nEntrée pour continuer...\n")

def print_step(title):
    print("="*60)
    print(title)
    print("="*60)

def show_response(resp):
    print(f"Code HTTP: {resp.status_code}")
    try:
        print("Réponse:", resp.json())
    except Exception:
        print("Réponse brute:", resp.text)

# 1. Facturation
print_step("1.a Création d’une facture")
resp = requests.post(f"{BASE_URL}/billing/process", params={
    "patientId": 1, "doctorId": 2, "treatments": "CONSULTATION,XRAY"
})
show_response(resp)
wait()

print_step("1.b Vérification de l’intégrité des factures")
resp = requests.get(f"{BASE_URL}/billing/integrity-report")
show_response(resp)
wait()

print_step("1.c Modification du prix d’un acte")
resp = requests.put(f"{BASE_URL}/billing/price", params={
    "treatment": "CONSULTATION", "price": 75.0
})
show_response(resp)
wait()

print_step("1.d Consultation des prix")
resp = requests.get(f"{BASE_URL}/billing/prices")
show_response(resp)
wait()

print_step("1.e Calcul d’un remboursement assurance")
resp = requests.get(f"{BASE_URL}/billing/insurance", params={"amount": 1000.0})
show_response(resp)
wait()

print_step("1.f Liste des factures en attente")
resp = requests.get(f"{BASE_URL}/billing/pending")
show_response(resp)
wait()

# 2. Prescriptions
print_step("2.a Création d’une prescription")
resp = requests.post(f"{BASE_URL}/prescriptions/add", params={
    "patientId": 1, "medicines": ["PARACETAMOL"], "notes": "Prendre 2x/jour"
})
show_response(resp)
wait()

print_step("2.b Récupération des prescriptions d’un patient")
resp = requests.get(f"{BASE_URL}/prescriptions/patient/1")
show_response(resp)
wait()

print_step("2.c Consultation de l’inventaire des médicaments")
resp = requests.get(f"{BASE_URL}/prescriptions/inventory")
show_response(resp)
wait()

print_step("2.d Rechargement du stock d’un médicament")
resp = requests.post(f"{BASE_URL}/prescriptions/refill", params={
    "medicine": "PARACETAMOL", "quantity": 10
})
show_response(resp)
wait()

print_step("2.e Calcul du coût d’une prescription (id=1)")
resp = requests.get(f"{BASE_URL}/prescriptions/cost/1")
show_response(resp)
wait()

print_step("2.f Suppression de toutes les données de prescription")
resp = requests.delete(f"{BASE_URL}/prescriptions/clear")
show_response(resp)
wait()

# 3. Inventaire
print_step("3.a Consultation de la quantité d’un produit (id=1)")
resp = requests.get(f"{BASE_URL}/inventory/1/quantity")
show_response(resp)
wait()



print("Scénario terminé")