pragma solidity ^0.8.17;

// This is the main contract for the ESG investment tracker.
contract ESGTracker {
  // The address of the contract owner.
  address public owner;

  // The list of all tracked investments.
  Investment[] public investments;

  // The event that is emitted when a new investment is added.
  event NewInvestment(
    uint id,
    address investor,
    string name,
    string ticker
  );

  // The event that is emitted when ESG data is added for an investment.
  event ESGDataAdded(
    uint id,
    uint carbonEmissions,
    uint diversityScore,
    uint governanceScore
  );

  // The struct for representing a tracked investment.
  struct Investment {
    uint id;
    address investor;
    string name;
    string ticker;
    uint carbonEmissions;
    uint diversityScore;
    uint governanceScore;
  }

  // The constructor for the contract.
  constructor() public {
    owner = msg.sender;
  }

  // The function for adding a new investment to the tracker.
  function addInvestment(string memory name, string memory ticker) public {
    Investment memory investment = Investment({
      id: investments.length,
      investor: msg.sender,
      name: name,
      ticker: ticker,
      carbonEmissions: 0,
      diversityScore: 0,
      governanceScore: 0
    });
    investments.push(investment);

    emit NewInvestment(
      investment.id,
      investment.investor,
      investment.name,
      investment.ticker
    );
  }

  // The function for adding ESG data for an investment.
  function addESGData(
    uint id,
    uint carbonEmissions,
    uint diversityScore,
    uint governanceScore
  ) public {
    Investment storage investment = investments[id];
    require(
      investment.investor == msg.sender,
      "Only the investor can add ESG data for an investment."
    );

    investment.carbonEmissions = carbonEmissions;
    investment.diversityScore = diversityScore;
    investment.governanceScore = governanceScore;

    emit ESGDataAdded(
      investment.id,
      investment.carbonEmissions,
      investment.diversityScore,
      investment.governanceScore
    );
  }
}
