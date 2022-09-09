package me.purplefishh.dipcraft.superbet.event;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import me.purplefishh.dipcraft.superbet.main.Main;
import me.purplefishh.dipcraft.superbet.resorce.Repleace;
import me.purplefishh.dipcraft.superbet.resorce.Resorce;
import me.purplefishh.dipcraft.superbet.utils.BaniInv;
import me.purplefishh.dipcraft.superbet.utils.TimeUntilStart;
import net.milkbowl.vault.economy.Economy;


public class BetEvent implements Listener {

	public List<Player> intentionalbet = new ArrayList<Player>();
	@EventHandler
	public void PutMoneyEvent(InventoryClickEvent e) {
		if (e.getView().getTitle().equals(Repleace.repleace(Resorce.bet_inv_name())) && e.getCurrentItem() != null) {
			ItemStack item = e.getCurrentItem();
			Player p = (Player) e.getWhoClicked();
			if (item.equals(Resorce.main_bg()) || item.equals(Resorce.line_bg()))
				e.setCancelled(true);
			// The finish button
			if (item.equals(Resorce.put_item())) {
				// Verify if he wants to bet when his amount = 0
				if (!Resorce.pariubani.containsKey(p) || Resorce.pariubani.get(p) == 0) {
					p.sendMessage(Resorce.no_money_bet());
				} else {
					
					// Place Bet
					if (Resorce.separate_roulette()) {
						if (TimeUntilStart.starts.containsKey(p) && TimeUntilStart.starts.get(p) == false)
							TimeUntilStart.start(p, BetOpen.invs.get(p));
						removemoney(p, Resorce.pariubani.get(p));
						intentionalbet.add(p);
						e.getWhoClicked().openInventory(BetOpen.invs.get(p));
					} else {
						if (TimeUntilStart.start == false)
							TimeUntilStart.start(p, Main.inv);
						removemoney(p, Resorce.pariubani.get(p));
						intentionalbet.add(p);
						p.openInventory(Main.inv);
					}
				}

				e.setCancelled(true);
				return;
			}
			// Cancel Button
			if (item.equals(Resorce.cancel_item())) {
				if (Resorce.separate_roulette())
					p.openInventory(BetOpen.invs.get(e.getWhoClicked()));
				else
					p.openInventory(Main.inv);
				Resorce.pariubani.remove(p);
				Resorce.pariu.remove(p);
				e.setCancelled(true);
				return;
			}
			
			int bani = money(e.getSlot());
			if (Resorce.pariubani.keySet().contains(p) == false)
				Resorce.pariubani.put(p, 0);
			int sumapariu = Resorce.pariubani.get(p);
			if (e.getSlot() >= 10 && e.getSlot() <= 16) {
				sumapariu += bani;
				if (sumapariu > getmoney(p))
					sumapariu = (int) getmoney(p);
				p.sendMessage(Resorce.money_select(BaniInv.punct(sumapariu)));
			}
			if (e.getSlot() >= 19 && e.getSlot() <= 25) {
				if (sumapariu == 0) {
					p.sendMessage(Resorce.make_less_zero());
					e.setCancelled(true);
					return;
				}
				sumapariu -= bani;
				if (sumapariu < 0)
					sumapariu = 0;
				p.sendMessage(Resorce.money_select(BaniInv.punct(sumapariu)));
			}

			int color = Resorce.pariu.get(p);
			if (color == 1) {
				Resorce.blackpariu -= Resorce.pariubani.get(p);
				changebuton(1, Resorce.blackpariu, p);
			}
			if (color == 2) {
				Resorce.redpariu -= Resorce.pariubani.get(p);
				changebuton(2, Resorce.redpariu, p);
			}
			if (color == 3) {
				Resorce.greenpariu -= Resorce.pariubani.get(p);
				changebuton(3, Resorce.greenpariu, p);
			}

			Resorce.pariubani.replace(p, sumapariu);

			if (color == 1) {
				Resorce.blackpariu += Resorce.pariubani.get(p);
				changebuton(1, Resorce.blackpariu, p);
			}
			if (color == 2) {
				Resorce.redpariu += Resorce.pariubani.get(p);
				changebuton(2, Resorce.redpariu, p);
			}
			if (color == 3) {
				Resorce.greenpariu += Resorce.pariubani.get(p);
				changebuton(3, Resorce.greenpariu, p);
			}

			e.setCancelled(true);
		}
	}

	@EventHandler
	public void exit(InventoryCloseEvent e) {
		if (e.getView().getTitle().equals(Repleace.repleace(Resorce.bet_inv_name()))) {
			if (Resorce.pariubani.containsKey(e.getPlayer()) && Resorce.pariubani.get(e.getPlayer()) != 0) {
				Player p = (Player) e.getPlayer();
				if (!intentionalbet.contains(p)) {
					int color = Resorce.pariu.get(p);
					if (color == 1) {
						Resorce.blackpariu -= Resorce.pariubani.get(p);
						changebuton(1, Resorce.blackpariu, p);
					}
					if (color == 2) {
						Resorce.redpariu -= Resorce.pariubani.get(p);
						changebuton(2, Resorce.redpariu, p);
					}
					if (color == 3) {
						Resorce.greenpariu -= Resorce.pariubani.get(p);
						changebuton(3, Resorce.greenpariu, p);
					}
					Resorce.pariu.remove(e.getPlayer());
					Resorce.pariubani.remove(e.getPlayer());
				} else
					intentionalbet.remove(p);
			}
		}
	}

	@SuppressWarnings({ "deprecation" })
	public double getmoney(Player p) {
		Economy eco = Main.getEconomy();
		double money = 0;
		money = eco.getBalance(p.getName());
		return money;
	}

	@SuppressWarnings({ "deprecation" })
	public void removemoney(Player p, int sum) {
		Economy eco = Main.getEconomy();
		// double money = (int) getmoney(p);
		// eco.setMoney(p.getName(), money - sum);
		eco.withdrawPlayer(p.getName(), sum);

	}

	int money(int p) {
		switch (p) {
		case 10:
			return Resorce.bet_amount(7);
		case 11:
			return Resorce.bet_amount(6);
		case 12:
			return Resorce.bet_amount(5);
		case 13:
			return Resorce.bet_amount(4);
		case 14:
			return Resorce.bet_amount(3);
		case 15:
			return Resorce.bet_amount(2);
		case 16:
			return Resorce.bet_amount(1);
		case 19:
			return Resorce.bet_amount(7);
		case 20:
			return Resorce.bet_amount(6);
		case 21:
			return Resorce.bet_amount(5);
		case 22:
			return Resorce.bet_amount(4);
		case 23:
			return Resorce.bet_amount(3);
		case 24:
			return Resorce.bet_amount(2);
		case 25:
			return Resorce.bet_amount(1);

		}
		return 0;
	}

	public static void changebuton(int cod, int suma, Player p) {
		if (!Resorce.separate_roulette()) {
			if (cod == 1)
				Main.inv.setItem(4 * 9 + 6, Resorce.black_button(suma));
			if (cod == 2)
				Main.inv.setItem(4 * 9 + 2, Resorce.red_button(suma));
			if (cod == 3)
				Main.inv.setItem(4 * 9 + 4, Resorce.green_button(suma));
		} else {
			if (cod == 1)
				BetOpen.invs.get(p).setItem(4 * 9 + 6, Resorce.black_button(suma));
			if (cod == 2)
				BetOpen.invs.get(p).setItem(4 * 9 + 2, Resorce.red_button(suma));
			if (cod == 3)
				BetOpen.invs.get(p).setItem(4 * 9 + 4, Resorce.green_button(suma));
		}
	}

}
