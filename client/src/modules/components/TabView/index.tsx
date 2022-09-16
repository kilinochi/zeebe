/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. Licensed under a proprietary license.
 * See the License.txt file for more information. You may not use this file
 * except in compliance with the proprietary license.
 */

import {useEffect, useState} from 'react';
import {PanelHeader} from 'modules/components/PanelHeader';
import {Container, Header, Tab} from './styled';
import {tracking} from 'modules/tracking';

type TabType = {
  id: string;
  label: string;
  content: React.ReactNode;
};

type Props = {
  tabs: TabType[];
  eventName?: 'variables-panel-used';
  dataTestId?: string;
};

const TabView: React.FC<Props> = ({tabs = [], eventName, dataTestId}) => {
  const [selectedTab, setSelectedTab] = useState<TabType | null>(null);

  useEffect(() => {
    setSelectedTab(selectedTab ?? tabs[0] ?? null);
  }, [tabs, selectedTab]);

  useEffect(() => {
    if (eventName !== undefined && selectedTab?.id !== undefined) {
      tracking.track({
        eventName,
        toTab: selectedTab.id,
      });
    }
  }, [selectedTab, eventName]);

  return (
    <Container data-testid={dataTestId}>
      {tabs.length === 1 ? (
        <>
          <PanelHeader title={tabs[0]!.label}></PanelHeader>
          {tabs[0]!.content}
        </>
      ) : (
        <>
          <Header>
            {tabs.map((tab) => (
              <Tab
                key={tab.id}
                isSelected={selectedTab?.id === tab.id}
                onClick={() => {
                  setSelectedTab(tab);
                }}
              >
                {tab.label}
              </Tab>
            ))}
          </Header>
          {selectedTab?.content}
        </>
      )}
    </Container>
  );
};

export {TabView};
