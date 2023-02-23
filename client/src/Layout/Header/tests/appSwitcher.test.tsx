/*
 * Copyright Camunda Services GmbH and/or licensed to Camunda Services GmbH
 * under one or more contributor license agreements. Licensed under a proprietary license.
 * See the License.txt file for more information. You may not use this file
 * except in compliance with the proprietary license.
 */

import {render, screen} from 'modules/testing-library';
import {nodeMockServer} from 'modules/mockServer/nodeMockServer';
import {mockGetCurrentUserWithC8Links} from 'modules/queries/get-current-user';
import {graphql} from 'msw';
import {Header} from '..';
import {Wrapper} from './mocks';
import {DEFAULT_MOCK_CLIENT_CONFIG} from 'modules/mocks/window';

describe('App switcher', () => {
  beforeAll(() => {
    global.IS_REACT_ACT_ENVIRONMENT = false;
  });

  afterAll(() => {
    global.IS_REACT_ACT_ENVIRONMENT = true;
  });

  afterEach(() => {
    window.clientConfig = DEFAULT_MOCK_CLIENT_CONFIG;
  });

  it('should render with correct links', async () => {
    window.clientConfig = {
      ...window.clientConfig,
      isEnterprise: false,
      organizationId: 'some-organization-id',
    };

    nodeMockServer.use(
      graphql.query('GetCurrentUser', (_, res, ctx) => {
        return res(ctx.data(mockGetCurrentUserWithC8Links));
      }),
    );

    const {user} = render(<Header />, {
      wrapper: Wrapper,
    });

    await user.click(
      await screen.findByRole('button', {
        name: /app switcher/i,
      }),
    );

    expect(await screen.findByRole('link', {name: 'Console'})).toHaveAttribute(
      'href',
      'https://link-to-console',
    );
    expect(screen.getByRole('link', {name: 'Modeler'})).toHaveAttribute(
      'href',
      'https://link-to-modeler',
    );
    expect(screen.getByRole('link', {name: 'Operate'})).toHaveAttribute(
      'href',
      'https://link-to-operate',
    );
    expect(screen.getByRole('link', {name: 'Tasklist'})).toHaveAttribute(
      'href',
      '/',
    );
    expect(screen.getByRole('link', {name: 'Optimize'})).toHaveAttribute(
      'href',
      'https://link-to-optimize',
    );
  });

  it('should not render links for CCSM', async () => {
    window.clientConfig = {
      ...window.clientConfig,
      isEnterprise: false,
      organizationId: null,
    };

    nodeMockServer.use(
      graphql.query('GetCurrentUser', (_, res, ctx) => {
        return res(ctx.data(mockGetCurrentUserWithC8Links));
      }),
    );

    render(<Header />, {
      wrapper: Wrapper,
    });

    expect(
      screen.queryByRole('link', {name: 'Console'}),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole('link', {name: 'Modeler'}),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole('link', {name: 'Operate'}),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole('link', {name: 'Tasklist'}),
    ).not.toBeInTheDocument();
    expect(
      screen.queryByRole('link', {name: 'Optimize'}),
    ).not.toBeInTheDocument();
  });
});
