function toggleAddSquadPanel() {
    var addSquadPanel = document.getElementById("addSquadPanel");
    if (addSquadPanel.className.includes("is-hidden")) {
        addSquadPanel.className = addSquadPanel.className.replace("is-hidden", "");
    }
}