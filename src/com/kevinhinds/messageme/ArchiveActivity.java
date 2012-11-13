package com.kevinhinds.messageme;

import java.util.Iterator;
import java.util.List;

import com.kevinhinds.messageme.item.ItemsDataSource;
import com.kevinhinds.messageme.itemlist.Itemlist;
import com.kevinhinds.messageme.itemlist.ItemlistDataSource;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Archived Activity for items
 * 
 * @author khinds
 */
public class ArchiveActivity extends Activity {

	private ItemsDataSource itemsDataSource;
	private ItemlistDataSource itemlistDataSource;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.archive);

		/** open data connections */
		openDataConnections();

		/** setup the list of items to show the user */
		setupItemsList();

		TextView CurrentMessages = (TextView) findViewById(R.id.CurrentMessages);
		CurrentMessages.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, ItemsActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});

		TextView ArchivedMessages = (TextView) findViewById(R.id.ArchivedMessages);
		ArchivedMessages.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(ArchiveActivity.this, ArchiveActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				startActivity(intent);
			}
		});

	}

	/**
	 * create the list of items on the activity as they currently are saved in the database
	 */
	private void setupItemsList() {

		/** get all the itemlist items saved in the DB */
		final List<Itemlist> itemlist = itemlistDataSource.getAllItems();

		/** attach to the LinearLayout to add TextViews dynamically via menuValues */
		LinearLayout ll = (LinearLayout) findViewById(R.id.archiveLayout);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);

		/** start with a clean slate */
		ll.removeAllViews();

		/** iterate over the itemlist to build the menu items to show */
		Iterator<Itemlist> itemlistiterator = itemlist.iterator();
		int i = 0;
		while (itemlistiterator.hasNext()) {
			i++;
			Itemlist itemlistitem = itemlistiterator.next();
			String itemName = itemlistitem.getName();

			TextView tv = new TextView(this);
			tv.setId(i);
			tv.setTextSize(18);
			tv.setText((CharSequence) itemName);
			tv.setLayoutParams(lp);
			tv.setClickable(true);
			tv.setTextColor(Color.BLACK);
			tv.setPadding(10, 8, 0, 8);
			tv.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.remove), null, null, null);
			tv.setOnClickListener(new OnClickListener() {
				/** depending on the item clicked, send it over to the searchActivity with the item clicked as "searchType" */
				public void onClick(View v) {
					TextView tv = (TextView) v;
					String itemValue = (String) tv.getText();

					final TextView itemInQuestion = new TextView(ArchiveActivity.this);
					itemInQuestion.setPadding(10, 0, 10, 10);
					itemInQuestion.setText((CharSequence) itemValue);
					itemInQuestion.setTextSize(18);
					new AlertDialog.Builder(ArchiveActivity.this).setTitle("Remove Item").setMessage((CharSequence) "Sure you with to remove?").setView(itemInQuestion)
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
									String enterValue = (String) itemInQuestion.getText();
									if (enterValue.length() != 0) {
										itemlistDataSource.deleteItemByName(enterValue);
										itemsDataSource.deleteItemByName(enterValue);
										ArchiveActivity.this.setupItemsList();
									}
								}
							}).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {
								}
							}).show();
				}
			});
			ll.addView(tv);
		}
	}

	/**
	 * open our data connections to items/itemslist
	 */
	private void openDataConnections() {
		itemsDataSource = new ItemsDataSource(this);
		itemsDataSource.open();
		itemlistDataSource = new ItemlistDataSource(this);
		itemlistDataSource.open();
	}
}
