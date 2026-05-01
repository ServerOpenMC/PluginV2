package fr.openmc.core.features.milestones;

import java.util.ArrayList;
import java.util.List;

public record MilestoneSnake(List<Integer> nodes, List<Integer> links) {
	
	public static MilestoneSnake buildSnake(int count, int[] columns, int startRow, int endRow) {
		List<Integer> nodes = new ArrayList<>();
		List<Integer> links = new ArrayList<>();
		int placed = 0;
		
		for (int colIdx = 0; colIdx < columns.length && placed < count; colIdx++) {
			int col = columns[colIdx];
			int nextPrimary = (colIdx + 1 < columns.length) ? columns[colIdx + 1] : -1;
			boolean topDown = (colIdx % 2 == 0);
			
			if (topDown) {
				// haut
				if (placed < count) {
					nodes.add(slotAt(startRow, col));
					placed++;
					if (placed < count) {
						for (int r = startRow + 1; r <= endRow - 1; r++)
							links.add(slotAt(r, col));
					}
				}
				// bas
				if (placed < count) {
					nodes.add(slotAt(endRow, col));
					placed++;
					if (placed < count && nextPrimary != -1) {
						for (int c = col + 1; c < nextPrimary; c++)
							links.add(slotAt(endRow, c));
					}
				}
			} else {
				// bas
				if (placed < count) {
					nodes.add(slotAt(endRow, col));
					placed++;
					if (placed < count) {
						for (int r = endRow - 1; r >= startRow + 1; r--)
							links.add(slotAt(r, col));
					}
				}
				
				// haut
				if (placed < count) {
					nodes.add(slotAt(startRow, col));
					placed++;
					if (placed < count && nextPrimary != -1) {
						for (int c = col + 1; c < nextPrimary; c++)
							links.add(slotAt(startRow, c));
					}
				}
			}
		}
		return new MilestoneSnake(nodes, links);
	}
	
	private static int slotAt(int row, int col) {
		return row * 9 + col;
	}
}