package com.discworld.booksbag.dto;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.discworld.booksbag.R;

public class BooksAdapter extends ExpandableRecyclerAdapter<BooksAdapter.BookListItem>
{
   private int iAllChildrendCount;
   
   private String sFilter;
   
   private ArrayList<ListItem> alListItemsNotFiltered; 
   
   private OnClickListener onClickListener = null;

   private OnLongClickListener onLongClickListener = null;   

   public BooksAdapter(Context context, ArrayList<ParentResult> alParentsResults)
   {
      super(context);

      setItems(generateItems(alParentsResults));
   }

   @Override
   public long getItemId(int i)
   {
      return visibleItems.get(i).id;
   }

   private List<BookListItem> generateItems(ArrayList<ParentResult> alParentsResults)
   {
      iAllChildrendCount = 0;
      List<BookListItem> items = new ArrayList<>();
      for(ParentResult oParentResult : alParentsResults)
      {
         items.add(new BookListItem(oParentResult.getName()));
         iAllChildrendCount += oParentResult.getChildList().size(); 
         for(Result result : oParentResult.getChildList())
            items.add(new BookListItem(result.id, result.content));
      }

      alListItemsNotFiltered = new ArrayList<ExpandableRecyclerAdapter.ListItem>();
      alListItemsNotFiltered.addAll(items);
      return items;
   }

   public static class BookListItem extends ExpandableRecyclerAdapter.ListItem
   {
      public long id = -1;
      
      public BookListItem(String group)
      {
         super(TYPE_HEADER, group);
      }

      public BookListItem(long id, String item)
      {
         super(TYPE_ITEM, item);

         this.id = id;
      }
   }

   public class HeaderViewHolder extends ExpandableRecyclerAdapter.HeaderViewHolder
   {
      TextView name;

      public HeaderViewHolder(View view)
      {
         super(view, (ImageView) view.findViewById(R.id.item_arrow));

         name = (TextView) view.findViewById(R.id.item_header_name);
      }

      public void bind(int position)
      {
         super.bind(position);

         name.setText(visibleItems.get(position).sText);
      }
   }

   public class ItemViewHolder extends ExpandableRecyclerAdapter.ViewHolder
   {
      public View view;
      
      TextView name;

      public ItemViewHolder(View view)
      {
         super(view);

         name = (TextView) view.findViewById(R.id.item_name);
         this.view = view;
      }

      public void bind(int position)
      {
         name.setText(visibleItems.get(position).sText);
      }
   }

   @Override
   public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
   {
      switch (viewType)
      {
         case TYPE_HEADER:
            return new HeaderViewHolder(inflate(R.layout.item_header, parent));
         case TYPE_ITEM:
         default:
            return new ItemViewHolder(inflate(R.layout.item_person, parent));
      }
   }

   @Override
   public void onBindViewHolder(ExpandableRecyclerAdapter.ViewHolder holder,
                                int position)
   {
      switch (getItemViewType(position))
      {
         case TYPE_HEADER:
            ((HeaderViewHolder) holder).bind(position);
         break;
         case TYPE_ITEM:
         default:
            ((ItemViewHolder) holder).bind(position);
            ((ItemViewHolder) holder).view.setOnClickListener(onClickListener);
            ((ItemViewHolder) holder).view.setOnLongClickListener(onLongClickListener);

         break;
      }
   }

   public void filter(String charText)
   {
      charText = charText.toLowerCase(Locale.getDefault());
      sFilter = charText;
      visibleItems.clear();
      if(charText.length() == 0)
      {
         allItems.addAll(alListItemsNotFiltered);
      } 
      else
      {
         for(BookListItem oBookListItem : allItems)
         {
            if(oBookListItem.ItemType == TYPE_HEADER || oBookListItem.sText.toLowerCase(Locale.getDefault()).contains(charText))
            {
               /*
                * If the last and the next items are headers remove the last item - it has not subitems  
                */
               if(oBookListItem.ItemType == TYPE_HEADER && visibleItems.get(visibleItems.size()-1).ItemType == TYPE_HEADER)
                  visibleItems.remove(visibleItems.size()-1);
               visibleItems.add(oBookListItem);
            }
         }
      }
      notifyDataSetChanged();   
   }

   public int getAllChildrenCount()
   {
      return iAllChildrendCount;
   }

   public void setClickListener(OnClickListener onClickListener)
   {
      this.onClickListener = onClickListener;
   }

   public void setLongClickListener(OnLongClickListener onLongClickListener)
   {
      this.onLongClickListener = onLongClickListener;
   }

   public void removeAt(int iClickedItemNdx)
   {
      removeItemAt(iClickedItemNdx);
   }

   @Override
   protected void removeItemAt(int visiblePosition)
   {
      super.removeItemAt(visiblePosition);
      if(visibleItems.get(visiblePosition-1).ItemType == TYPE_HEADER && (visiblePosition == visibleItems.size() || visibleItems.get(visiblePosition).ItemType == TYPE_HEADER))
         super.removeItemAt(visiblePosition-1);
   }
   
   public boolean isExpandAll()
   {
      return visibleItems.size() == allItems.size();
   }
   
   
}