package tk.ubublik.huffmancoding.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

@Deprecated
public class ListStatsTreeUpdater implements TreeUpdater {

    @Override
    public void update(Leaf tree) {
        List<List<Pair<Leaf, Leaf>>> treeList = getChildrenLeafs(Utils.getSingleValueList(new Pair<>(tree, null)));
        ListIterator<List<Pair<Leaf, Leaf>>> treeRowsIterator = treeList.listIterator(treeList.size());
        List<Pair<Leaf, Leaf>> currentRow = treeRowsIterator.previous(), parentRow;
        int rowIndex = treeList.size();
        while (treeRowsIterator.hasPrevious()) {
            rowIndex--;
            parentRow = treeRowsIterator.previous();
            boolean somethingChanged;
            do {
                somethingChanged = false;
                //System.out.println(leaf.toString());
                Pair<Leaf, Leaf> maxChildPair = null, minParentPair = null;
                for (Pair<Leaf, Leaf> pair : currentRow) {
                    if (maxChildPair == null || (pair.first.getWeight() > maxChildPair.first.getWeight()))
                        maxChildPair = pair;
                }
                for (Pair<Leaf, Leaf> pair : parentRow) {
                    if (minParentPair == null || (pair.first.getWeight() < minParentPair.first.getWeight()))
                        minParentPair = pair;
                }
                if (maxChildPair != null &&
                        minParentPair != null &&
                        (maxChildPair.first.getWeight() - minParentPair.first.getWeight() > 0)) {
                    somethingChanged = true;
                    maxChildPair = maxChildPair.clone();
                    minParentPair = minParentPair.clone();
                    swapPairs(parentRow, currentRow, maxChildPair, minParentPair);

                    updateBranchWeight(treeList, minParentPair, rowIndex);//maybe -1
                    updateBranchWeight(treeList, maxChildPair, rowIndex-1);
                    treeList = getChildrenLeafs(Utils.getSingleValueList(new Pair<>(tree, null)));
                    treeRowsIterator = treeList.listIterator(rowIndex);
                }
            } while (somethingChanged);
            currentRow = parentRow;
        }
    }

    private void swapPairs(List<Pair<Leaf, Leaf>> parentRow, List<Pair<Leaf, Leaf>> currentRow, Pair<Leaf, Leaf> maxChildPair, Pair<Leaf, Leaf> minParentPair){
        Leaf minParentLeaf = minParentPair.first, maxChildrenLeaf = maxChildPair.first;
        Leaf parentOfParentPair = minParentPair.second, parentOfChildPair = maxChildPair.second;
        boolean childDirection = maxChildrenLeaf == maxChildPair.second.getRight();
        boolean parentDirection = minParentLeaf == minParentPair.second.getRight();

        if (childDirection) maxChildPair.second.setRight(minParentLeaf);
        else maxChildPair.second.setLeft(minParentLeaf);

        if (parentDirection) minParentPair.second.setRight(maxChildrenLeaf);
        else minParentPair.second.setLeft(maxChildrenLeaf);

        maxChildPair.second = parentOfParentPair;
        minParentPair.second = parentOfChildPair;
        /*for (Pair<Leaf, Leaf> listPair: parentRow){
            if (listPair.first == minParentLeaf){
                listPair.first = maxChildrenLeaf;
                break;
            }
        }

        for (Pair<Leaf, Leaf> listPair: currentRow){
            if (listPair.second == minParentLeaf){
                listPair.second = maxChildrenLeaf;
            }
            if (listPair.first == )
        }*/
    }

    private void updateBranchWeight(List<List<Pair<Leaf, Leaf>>> treeList, Pair<Leaf, Leaf> changedPair, int rowIndex) {
        ListIterator<List<Pair<Leaf, Leaf>>> iterator = treeList.listIterator(rowIndex);
        changedPair.second.fixWeight();
        while (iterator.hasPrevious()) {
            List<Pair<Leaf, Leaf>> row = iterator.previous();
            for (Pair<Leaf, Leaf> pair : row) {
                if (pair.first == changedPair.second) {
                    changedPair = pair;
                    break;
                }
            }
            if (changedPair.second == null ||
                    changedPair.second.getCharacter() != null ||
                    !changedPair.second.fixWeight())
                break;
        }
    }

    //list/list/pair/leaf-leaf -> tree/level/child-parent
    private List<List<Pair<Leaf, Leaf>>> getChildrenLeafs(List<Pair<Leaf, Leaf>> list) {
        List<Pair<Leaf, Leaf>> childList = new ArrayList<>();
        for (Pair<Leaf, Leaf> pair : list) {
            if (pair.first.getLeft() != null)
                childList.add(new Pair<>(pair.first.getLeft(), pair.first));
            if (pair.first.getRight() != null)
                childList.add(new Pair<>(pair.first.getRight(), pair.first));
        }
        if (childList.size() == 0) return Utils.getSingleValueList(list);
        List<List<Pair<Leaf, Leaf>>> moreChildrenLeafs = getChildrenLeafs(childList);
        moreChildrenLeafs.add(0, list);
        return moreChildrenLeafs;
    }
}
